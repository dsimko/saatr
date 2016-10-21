
package org.jboss.qa.tool.saatr.web.comp.build;

import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.CheckedFolder;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.domain.build.Build;

@SuppressWarnings("serial")
public class TreeCheckedFolder extends CheckedFolder<Build> {

    public TreeCheckedFolder(String id, AbstractTree<Build> tree, IModel<Build> model) {
        super(id, tree, model);
    }

}
