
package org.jboss.qa.tool.saatr.web.comp.build;

import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.CheckedFolder;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.web.comp.build.JobRunsTreePanel.JobRunDto;

@SuppressWarnings("serial")
public class TreeCheckedFolder extends CheckedFolder<JobRunDto> {

    public TreeCheckedFolder(String id, AbstractTree<JobRunDto> tree, IModel<JobRunDto> model) {
        super(id, tree, model);
    }

}
