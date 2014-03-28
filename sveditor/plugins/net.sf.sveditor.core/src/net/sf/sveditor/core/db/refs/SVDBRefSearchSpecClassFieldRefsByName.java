package net.sf.sveditor.core.db.refs;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eclipse.core.runtime.NullProgressMonitor;

import net.sf.sveditor.core.db.ISVDBChildItem;
import net.sf.sveditor.core.db.ISVDBChildParent;
import net.sf.sveditor.core.db.ISVDBItemBase;
import net.sf.sveditor.core.db.SVDBClassDecl;
import net.sf.sveditor.core.db.SVDBItem;
import net.sf.sveditor.core.db.SVDBItemType;
import net.sf.sveditor.core.db.SVDBLocation;
import net.sf.sveditor.core.db.SVDBTask;
import net.sf.sveditor.core.db.SVDBTypeInfo;
import net.sf.sveditor.core.db.SVDBTypeInfoUserDef;
import net.sf.sveditor.core.db.expr.SVDBTFCallExpr;
import net.sf.sveditor.core.db.index.ISVDBIndexIterator;
import net.sf.sveditor.core.db.search.SVDBFindByNameInScopes;
import net.sf.sveditor.core.db.stmt.SVDBVarDeclItem;
import net.sf.sveditor.core.db.stmt.SVDBVarDeclStmt;
import net.sf.sveditor.core.log.LogFactory;
import net.sf.sveditor.core.log.LogHandle;

public class SVDBRefSearchSpecClassFieldRefsByName extends SVDBRefSearchSpecByNameBase {
	private ISVDBIndexIterator			fIndexIt;
	private ISVDBChildItem				fTarget;
	private LogHandle					fLog;
	private ISVDBChildParent			fFieldHost;
	private Set<String>					fClassExcludes;
	
	public SVDBRefSearchSpecClassFieldRefsByName(
			ISVDBIndexIterator 		index_it,
			ISVDBChildItem			target) {
		super(SVDBItem.getName(target), NameMatchType.Equals);
		
		fLog = LogFactory.getLogHandle("SVDBRefSearchSpecClassFieldRefsByName");
		fClassExcludes = new HashSet<String>();
		
		fTarget = target;
		
		fLog.debug("Target is: " + SVDBItem.getName(fTarget) + " " + fTarget.getType());
		
		// First, do a bit of pre-work
		if (fTarget.getType().isElemOf(SVDBItemType.Task, SVDBItemType.Function, SVDBItemType.VarDeclItem)) {
			fFieldHost = findTFHost(fTarget);
		
			if (fFieldHost != null && fFieldHost.getType() == SVDBItemType.ClassDecl &&
					fTarget.getType().isElemOf(SVDBItemType.Task, SVDBItemType.Function)) {
				// Determine the classes to include/exclude. We assume the TF handle
				// is the closest-to-base class that declares the TF
				findClassExcludes();
			}
		}
	}
	
	private ISVDBChildParent findTFHost(ISVDBChildItem tff) {
		ISVDBChildParent ret = null;
		
		ISVDBChildItem ci = tff.getParent();
		
		while (ci != null && 
				ci.getType().isElemOf(SVDBItemType.ClassDecl,SVDBItemType.InterfaceDecl,SVDBItemType.ModuleDecl)) {
			ci = ci.getParent();
		}
		
		if (ci != null) {
			ret = (ISVDBChildParent)ci;
		}
		
		return ret;
	}
	
	private void findClassExcludes() {
		SVDBClassDecl cls = (SVDBClassDecl)fFieldHost;
		
		if (cls.getSuperClass() != null) {
			// Find extension references to the super class
//			fIndexIt.findReferences(new NullProgressMonitor(), ref_spec, ref_matcher);
		}
	}

	@Override
	public boolean matches(
			SVDBLocation 			loc, 
			SVDBRefType 			type,
			Stack<ISVDBItemBase> 	scope, 
			String 					name) {
		ISVDBItemBase it = scope.peek();
		
		System.out.println("name=" + name + " it=" + it);
		
		if (it.getType() == SVDBItemType.TFCallExpr) {
			ISVDBItemBase next_to_last = null;
			
			if (scope.size() > 1) {
				next_to_last = scope.get(scope.size()-2);
			}
			System.out.println("name=" + name);
			if (name.equals(SVDBItem.getName(fTarget))) {
				SVDBTFCallExpr tf = (SVDBTFCallExpr)it;
				ISVDBChildItem enclosing_scope = findEnclosingScope(scope);
				System.out.println("get_parent: dumping scope target=" +
						((SVDBTFCallExpr)it).getTarget());
				for (int i=scope.size()-1; i>=0; i--) {
					System.out.println("  item[" + i + "] " + scope.get(i).getType());
				}
				if (tf.getTarget() != null && enclosing_scope != null) {
					SVDBFindByNameInScopes scopes_finder = new SVDBFindByNameInScopes(fIndexIt);
					System.out.println("Find:");
					List<ISVDBItemBase> items = scopes_finder.findItems(enclosing_scope, tf.getTarget().toString(), true);
					if (items.size() > 0 && items.get(0).getType() == SVDBItemType.VarDeclItem) {
						SVDBVarDeclItem var_it = (SVDBVarDeclItem)items.get(0);
						SVDBVarDeclStmt var_decl = (SVDBVarDeclStmt)var_it.getParent();
						
						SVDBTypeInfo type_info = var_decl.getTypeInfo();
						
						if (type_info.getType() == SVDBItemType.TypeInfoUserDef) {
							SVDBTypeInfoUserDef type_ud = (SVDBTypeInfoUserDef)type_info;
							System.out.println("Var typename: " + type_ud.getName());
						}
						
					}
					System.out.println("items=" + items);
				}
				return true;
			}
		}
		
		// TODO Auto-generated method stub
		return false;
	}
	
	private ISVDBChildItem findEnclosingScope(Stack<ISVDBItemBase> scope) {
		for (int i=scope.size()-2; i>=0; i--) {
			ISVDBItemBase it = scope.get(i);
			if (it.getType().isElemOf(SVDBItemType.Task, SVDBItemType.Function, 
					SVDBItemType.ClassDecl, SVDBItemType.ModuleDecl, SVDBItemType.InterfaceDecl,
					SVDBItemType.PackageDecl)) {
				return (ISVDBChildItem)it;
			}
		}
		return null;
	}

}
