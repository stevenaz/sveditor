/****************************************************************************
 * Copyright (c) 2008-2014 Matthew Ballance and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Ballance - initial implementation
 ****************************************************************************/


package net.sf.sveditor.ui.explorer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonMenuConstants;

import net.sf.sveditor.core.SVCorePlugin;
import net.sf.sveditor.core.builder.SVBuilderProcess;
import net.sf.sveditor.core.db.project.SVDBProjectData;
import net.sf.sveditor.core.log.ILogLevel;
import net.sf.sveditor.core.log.LogFactory;
import net.sf.sveditor.core.log.LogHandle;

public class RebuildSvIndexAction extends CommonActionProvider implements ILogLevel {

	public RebuildSvIndexAction() {
		fRebuildAction = new RebuildIndexAction();
	}
	
	public void fillContextMenu(IMenuManager menu) {
		menu.insertAfter(ICommonMenuConstants.GROUP_ADDITIONS, fRebuildAction);
	}
	
	private RebuildIndexAction			fRebuildAction;
	
	private static class RebuildIndexJob extends Job {
		private List<IProject>		fProjects;
		
		public RebuildIndexJob(List<IProject> projects) {
			super("Rebuild SV Index");
			fProjects = new ArrayList<IProject>(projects);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			SubMonitor sm = SubMonitor.convert(monitor, 1000*fProjects.size());
			sm.beginTask("Rebuild Indexes", 1000*fProjects.size());
			for (IProject proj : fProjects) {
				SubMonitor proj_m = sm.newChild(1000);
				SVDBProjectData pd = SVCorePlugin.getDefault().getProjMgr().getProjectData(proj);
				if (pd != null) {
					pd.buildEvent(true, IncrementalProjectBuilder.FULL_BUILD, null);
				}
				SVBuilderProcess p = new SVBuilderProcess(proj_m, proj);
				SVCorePlugin.getDefault().getBuildProcessListener().buildProcess(p);
				p.rebuild_project();
				proj_m.done();
				if (pd != null) {
					pd.buildEvent(false, IncrementalProjectBuilder.FULL_BUILD, null);
				}
			}
			sm.done();
			return Status.OK_STATUS;
		}
		
	}
	
	private class RebuildIndexAction extends SelectionListenerAction {
		private LogHandle					fLog;
		
		public RebuildIndexAction() {
			super("Rebuild SV Index");
			fLog = LogFactory.getLogHandle("RebuildIndexAction");
		}
		
		
		public void run() {
			IStructuredSelection sel_s = (IStructuredSelection)
				getActionSite().getViewSite().getSelectionProvider().getSelection();
			updateSelection(sel_s);

			List<IProject> projects = new ArrayList<IProject>();

			for (Object sel_o : sel_s.toList()) {
				IProject p = null;
				if (sel_o instanceof IProject) {
					p = (IProject)sel_o;
				} else if (sel_o instanceof IResource) {
					p = ((IResource)sel_o).getProject();
				}
				
				if (p != null && !projects.contains(p)) {
					projects.add(p);
				}
			}

			RebuildIndexJob job = new RebuildIndexJob(projects);
			job.schedule();
		}
	}

}
