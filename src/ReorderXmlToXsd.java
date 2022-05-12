import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.parser.XSOMParser;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;

public class ReorderXmlToXsd {
  public static void main(String[] args) throws Exception {
    File unorderedXml = new File(args[0]);
    File xsd = new File(args[1]);
    File orderedXml = new File(args[2]);

    XSOMParser p = new XSOMParser();
    p.parse(xsd);
    XSSchemaSet parsed = p.getResult();
    System.out.println(parsed);
    Builder xom = new Builder();
    Document unorderedDoc = xom.build(unorderedXml);
    Element unorderedRoot = unorderedDoc.getRootElement();
    System.out.println(unorderedRoot);
    XSElementDecl root = parsed.getElementDecl(
        unorderedRoot.getNamespaceURI(),
        unorderedRoot.getLocalName());

    XMLOutputFactory stax = XMLOutputFactory.newInstance();

    try (OutputStream to = new FileOutputStream(orderedXml)) {
      XMLStreamWriter using = stax.createXMLStreamWriter(to, "UTF-8");

      root.visit(
          new XSVisitorWriteOrdered(
              new WrappedDocument(unorderedDoc),
              new UncheckedXMLStreamWriter(using)));
    }
  }
}