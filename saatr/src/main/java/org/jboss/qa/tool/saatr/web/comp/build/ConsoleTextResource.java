
package org.jboss.qa.tool.saatr.web.comp.build;

import org.apache.wicket.request.resource.CharSequenceResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.build.ConsoleTextDocument;
import org.jboss.qa.tool.saatr.repo.build.ConsoleTextRepository;

@SuppressWarnings("serial")
public class ConsoleTextResource extends ResourceReference {

    public static final String CONTENT_TYPE = "text/plain";

    public static final String PATH = "/build/consoleText/";

    public static final String ID = "id";

    private final IResource resource;

    public ConsoleTextResource(ConsoleTextRepository repository) {
        super(ConsoleTextResource.class.getName());
        resource = new CharSequenceResource(CONTENT_TYPE) {

            @Override
            protected CharSequence getData(Attributes attributes) {
                String id = attributes.getParameters().get(ID).toOptionalString();
                if (id != null) {
                    ConsoleTextDocument document = repository.findOne(new ObjectId(id));
                    if (document != null) {
                        return document.getContent();
                    }
                }
                return super.getData(attributes);
            }
        };
    }

    @Override
    public IResource getResource() {
        return resource;
    }
}
