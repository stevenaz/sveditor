package net.sf.sveditor.ui.explorer;

import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;

import net.sf.sveditor.core.db.SVDBItemType;
import net.sf.sveditor.core.db.index.ISVDBIndexIterator;
import net.sf.sveditor.core.db.index.SVDBDeclCacheItem;
import net.sf.sveditor.core.db.search.SVDBFindByTypeMatcher;

public class PackagesExplorerData extends DeferredProjectDataProvider {

	public PackagesExplorerData(IProjectPathsData p) {
		super(p, "Packages");
	}

	public Object[] getChildrenDeferred(Object parent) {
		IProjectPathsData p = (IProjectPathsData)parent;
		while (p.getParent(p) != null) {
			p = (IProjectPathsData)p.getParent(p);
		}
		ISVDBIndexIterator index_it = ((ProjectPathsData)p).getIndexIt();

		List<SVDBDeclCacheItem> packages = index_it.findGlobalScopeDecl(
				new NullProgressMonitor(), "",
				new SVDBFindByTypeMatcher(SVDBItemType.PackageDecl));		

		DeclCacheItem children[] = new DeclCacheItem[packages.size()];
		for (int i=0; i<packages.size(); i++) {
			children[i] = new DeclCacheItem(this, packages.get(i));
		}
		
		return children;
	}


}
