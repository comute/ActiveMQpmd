/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.openwire.tool;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jam.JClass;
import org.codehaus.jam.JProperty;


/**
 *
 * @version $Revision: 379734 $
 */
public class CppHeadersGenerator extends CppClassesGenerator {

    protected String getFilePostFix() {
        return ".hpp";
    }
    
	protected void generateFile(PrintWriter out) {
		generateLicence(out);		
		
out.println("#ifndef ActiveMQ_"+className+"_hpp_");
out.println("#define ActiveMQ_"+className+"_hpp_");
out.println("");
out.println("// Turn off warning message for ignored exception specification");
out.println("#ifdef _MSC_VER");
out.println("#pragma warning( disable : 4290 )");
out.println("#endif");
out.println("");
out.println("#include <string>");
out.println("#include \"activemq/command/"+baseClass+".hpp\"");

		List properties = getProperties();
		for (Iterator iter = properties.iterator(); iter.hasNext();) {
			JProperty property = (JProperty) iter.next();
		    if( !property.getType().isPrimitiveType() &&
		        !property.getType().getSimpleName().equals("String") &&
		        !property.getType().getSimpleName().equals("ByteSequence") )
		    {
		        String includeName = toCppType(property.getType());
		        if( property.getType().isArrayType() )
		        {
		            JClass arrayType = property.getType().getArrayComponentType();
		            if( arrayType.isPrimitiveType() )
		                continue ;
		        }
		        if( includeName.startsWith("array<") )
		            includeName = includeName.substring(6, includeName.length()-1);
		        else if( includeName.startsWith("p<") )
		            includeName = includeName.substring(2, includeName.length()-1);
		
		        if( includeName.equals("IDataStructure") ) {
out.println("#include \"activemq/"+includeName+".hpp\"");
				}  else {
out.println("#include \"activemq/command/"+includeName+".hpp\"");
				}
		    }
		}
out.println("");
out.println("#include \"activemq/protocol/IMarshaller.hpp\"");
out.println("#include \"ppr/io/IOutputStream.hpp\"");
out.println("#include \"ppr/io/IInputStream.hpp\"");
out.println("#include \"ppr/io/IOException.hpp\"");
out.println("#include \"ppr/util/ifr/array\"");
out.println("#include \"ppr/util/ifr/p\"");
out.println("");
out.println("namespace apache");
out.println("{");
out.println("  namespace activemq");
out.println("  {");
out.println("    namespace command");
out.println("    {");
out.println("      using namespace ifr;");
out.println("      using namespace std;");
out.println("      using namespace apache::activemq;");
out.println("      using namespace apache::activemq::protocol;");
out.println("      using namespace apache::ppr::io;");
out.println("");
out.println("/*");
out.println(" *");
out.println(" *  Command and marshalling code for OpenWire format for "+className+"");
out.println(" *");
out.println(" *");
out.println(" *  NOTE!: This file is autogenerated - do not modify!");
out.println(" *         if you need to make a change, please see the Groovy scripts in the");
out.println(" *         activemq-core module");
out.println(" *");
out.println(" */");
out.println("class "+className+" : public "+baseClass+"");
out.println("{");
out.println("protected:");

		for (Iterator iter = properties.iterator(); iter.hasNext();) {
			JProperty property = (JProperty) iter.next();
	        String type = toCppType(property.getType());
	        String name = decapitalize(property.getSimpleName());
out.println("    "+type+" "+name+" ;");
	    }
out.println("");
out.println("public:");
out.println("    const static unsigned char TYPE = "+getOpenWireOpCode(jclass)+";");
out.println("");
out.println("public:");
out.println("    "+className+"() ;");
out.println("    virtual ~"+className+"() ;");
out.println("");
out.println("    virtual unsigned char getDataStructureType() ;");

		for (Iterator iter = properties.iterator(); iter.hasNext();) {
			JProperty property = (JProperty) iter.next();
	        String type = toCppType(property.getType());
	        String propertyName = property.getSimpleName();
	        String parameterName = decapitalize(propertyName);
out.println("");
out.println("    virtual "+type+" get"+propertyName+"() ;");
out.println("    virtual void set"+propertyName+"("+type+" "+parameterName+") ;");
	    }
out.println("");
out.println("    virtual int marshal(p<IMarshaller> marshaller, int mode, p<IOutputStream> ostream) throw (IOException) ;");
out.println("    virtual void unmarshal(p<IMarshaller> marshaller, int mode, p<IInputStream> istream) throw (IOException) ;");
out.println("} ;");
out.println("");
out.println("/* namespace */");
out.println("    }");
out.println("  }");
out.println("}");
out.println("");
out.println("#endif /*ActiveMQ_"+className+"_hpp_*/");
}    

}
