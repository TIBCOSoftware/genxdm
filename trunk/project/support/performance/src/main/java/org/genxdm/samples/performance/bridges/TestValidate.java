package org.genxdm.samples.performance.bridges;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.genxdm.Feature;
import org.genxdm.io.Resolved;
import org.genxdm.processor.w3c.xs.W3cXmlSchemaParser;
import org.genxdm.processor.w3c.xs.validation.XdmContentValidator;
import org.genxdm.processor.w3c.xs.validation.ValidatorFactory;
import org.genxdm.typed.TypedContext;
import org.genxdm.xs.ComponentBag;
import org.genxdm.xs.SchemaLoadOptions;
import org.genxdm.xs.exceptions.SchemaException;
import org.genxdm.xs.exceptions.SchemaExceptionCatcher;
import org.genxdm.xs.resolve.CatalogResolver;
import org.genxdm.xs.resolve.SchemaCatalog;

class TestValidate<N,A> extends BaseBridgePerfTest<N,A>
{
	public static final String SCHEMA_FILE_PROP_NAME = "bridge.validate.schema";
	public static final String COPY_TYPE_ANNOTATION_NAME = "bridge.validate.copyTypeAnnotations";
	private static final ArrayList<String> REQUIRED_FEATURES = new ArrayList<String>();
	static 
	{
		REQUIRED_FEATURES.add(Feature.TYPED);
	}
	String m_schemaFile;
	SchemaExceptionCatcher m_errors;
    XdmContentValidator<A> m_validator;
    boolean m_copyTypeAnnotations = false;
    
    @Override
    public Iterable<String> getRequiredFeatures()
    {
    	return REQUIRED_FEATURES;
    }
	@Override
	public String getName() {
		return "Validate";
	}
	@Override
	public void initialSetup(Properties props)	{
		super.initialSetup(props);
		
		m_schemaFile = props.getProperty(SCHEMA_FILE_PROP_NAME);
		if(m_schemaFile == null)
		{
			throw new IllegalStateException("Validation testing requires schema file location.");
		}
		String baseURI = props.getProperty(BridgePerfModule.BASE_URI_PROP_NAME);
		if(baseURI == null)
		{
			throw new IllegalStateException("Base URI must be specified.");
		}
		m_copyTypeAnnotations = Boolean.parseBoolean(props.getProperty(COPY_TYPE_ANNOTATION_NAME, "false"));

		// Load a schema...
		final TypedContext<N,A> typedContext = getPcx().getTypedContext();
        final String schemaFilePath = baseURI + "/" + m_schemaFile;
        try {
			addSchema(typedContext, new URI(schemaFilePath));
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
        
		// Create a validator...
		final ValidatorFactory<N, A> vcf = new ValidatorFactory<N, A>(typedContext);
		m_validator = vcf.newXdmContentValidator();
	}
	
	@Override
	public void iterativeSetup() {
		if(m_errors == null)
		{
			m_errors = new SchemaExceptionCatcher();
			m_validator.setSchemaExceptionHandler(m_errors);
		}
		else
		{
			m_errors.clear();
		}
	}
	@Override
	public void execute() {
		getModel().stream(getTestNode(), m_copyTypeAnnotations, m_validator);
	}
	@Override
	public Iterable<String> iterativeTeardown() {
		ArrayList<String> retval = new ArrayList<String>();
		if(!m_errors.isEmpty())
		{
			retval.add(m_errors.size() + " validation errors.");
			int errorCnt = 0;
			for (SchemaException ex : m_errors)
			{
				if(errorCnt == 10)
				{
					retval.add("\t" + "... that's all we're listing...");
					break;
				}
				errorCnt++;
				retval.add("\t" + ex.getMessage());
			}
		}
		return null; 
	}
	
	protected LinkedList<SchemaException> addSchema(TypedContext<N, A> tpcx, URI uri) throws Exception
	{
		// Load a top-level schema into the processing context.
		final List<Resolved<InputStream>> resources = new LinkedList<Resolved<InputStream>>();
		resources.add(getResolver().resolveInputStream(uri));

		final SchemaExceptionCatcher errors = new SchemaExceptionCatcher();
		final SchemaLoadOptions args = new SchemaLoadOptions();
		final W3cXmlSchemaParser parser = new W3cXmlSchemaParser();

		for (final Resolved<InputStream> resource : resources)
		{
			ComponentBag scBag = parser.parse(resource.getLocation(), resource.getResource(), resource.getSystemId(), errors, args, tpcx.getMetaBridge().getComponentProvider());
			if(!errors.isEmpty())
			{
				for(SchemaException error : errors)
				{
					System.out.println("      " + error.getLocalizedMessage());
				}
			}
			tpcx.register(scBag);
		}
		return errors;
	}
	class MyCatalog implements SchemaCatalog {

		private final HashMap<URI, URI> m_ns2loc = new HashMap<URI, URI>();
		
		@Override
		public URI resolveLocation(URI baseURI, URI schemaLocation) {
			return resolveNamespaceAndSchemaLocation(baseURI, null, schemaLocation);
		}

		@Override
		public URI resolveNamespaceAndSchemaLocation(URI baseURI, URI namespace, URI schemaLocation) {
			if(schemaLocation == null)
			{
				// No other way to look this up in this catalog.
				return m_ns2loc.get(namespace);
			}
			String x = baseURI.toString();
			int index = x.lastIndexOf('/');
			String uriString = x.substring(0, index + 1).concat(schemaLocation.toString());
			try {
				m_ns2loc.put(namespace, schemaLocation);
				return new URI(uriString);
			} catch (URISyntaxException e) {
				e.printStackTrace();
				return schemaLocation;
			}
		}
	}
	class MyResolver implements CatalogResolver {
		@Override
		public InputStream resolveInputStream(URI catalogURI)
				throws IOException {
			Resolved<InputStream> resolved = getResolver().resolveInputStream(catalogURI);
			return resolved.getResource();
		}
	}
}
