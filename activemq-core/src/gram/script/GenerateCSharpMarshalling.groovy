/**
 *
 * Copyright 2005-2006 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.apache.activemq.openwire.tool.OpenWireCSharpMarshallingScript

/**
 * Generates the C# marshalling code for the Open Wire Format
 *
 * @version $Revision$
 */
class GenerateCSharpMarshalling extends OpenWireCSharpMarshallingScript {

 	void generateFile(PrintWriter out) {
        out << """//
//
// Copyright 2005-2006 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

using System;
using System.Collections;
using System.IO;

using OpenWire.Client;
using OpenWire.Client.Commands;
using OpenWire.Client.Core;
using OpenWire.Client.IO;

namespace OpenWire.Client.IO
{
  //
  //  Marshalling code for Open Wire Format for ${jclass.simpleName}
  //
  //
  //  NOTE!: This file is autogenerated - do not modify!
  //        if you need to make a change, please see the Groovy scripts in the
  //        activemq-core module
  //
  public ${abstractClassText}class $className : $baseClass
  {
"""

if( !abstractClass ) out << """

    public override DataStructure CreateObject() 
    {
        return new ${jclass.simpleName}();
    }

    public override byte GetDataStructureType() 
    {
        return ${jclass.simpleName}.ID_${jclass.simpleName};
    }
"""

out << """
    // 
    // Un-marshal an object instance from the data input stream
    // 
    public override void Unmarshal(OpenWireFormat wireFormat, Object o, BinaryReader dataIn, BooleanStream bs) 
    {
        base.Unmarshal(wireFormat, o, dataIn, bs);
"""
 
if( !properties.isEmpty() || marshallerAware )  out << """
        ${jclass.simpleName} info = (${jclass.simpleName})o;
"""

if( marshallerAware ) out << """
        info.BeforeUnmarshall(wireFormat);
        
"""

generateUnmarshalBody(out)

if( marshallerAware ) out << """
        info.AfterUnmarshall(wireFormat);
"""

out << """
    }


    //
    // Write the booleans that this object uses to a BooleanStream
    //
    public override int Marshal1(OpenWireFormat wireFormat, Object o, BooleanStream bs) {
        ${jclass.simpleName} info = (${jclass.simpleName})o;
"""


if( marshallerAware ) out << """
        info.BeforeMarshall(wireFormat);
"""

out << """
        int rc = base.Marshal1(wireFormat, info, bs);
"""

def baseSize = generateMarshal1Body(out)
    
out << """
        return rc + ${baseSize};
    }

    // 
    // Write a object instance to data output stream
    //
    public override void Marshal2(OpenWireFormat wireFormat, Object o, BinaryWriter dataOut, BooleanStream bs) {
        base.Marshal2(wireFormat, o, dataOut, bs);
"""

if( !properties.isEmpty() || marshallerAware ) out << """
        ${jclass.simpleName} info = (${jclass.simpleName})o;
"""

generateMarshal2Body(out)

if( marshallerAware ) out << """
        info.AfterMarshall(wireFormat);
"""

out << """
    }
  }
}
"""
        }
 	
 
    void generateFactory(PrintWriter out) {
        out << """//
//
// Copyright 2005-2006 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

// Marshalling code for Open Wire Format for ${jclass.simpleName}
//
//
// NOTE!: This file is autogenerated - do not modify!
//        if you need to make a change, please see the Groovy scripts in the
//        activemq-openwire module
//

using System;
using System.Collections;
using System.IO;

using OpenWire.Client;
using OpenWire.Client.Commands;
using OpenWire.Client.Core;
using OpenWire.Client.IO;

namespace OpenWire.Client.IO
{
    public class MarshallerFactory
    {
        public void configure(OpenWireFormat format) 
        {
"""

    for (jclass in concreteClasses) {
	    out << """
            format.addMarshaller(new ${jclass.simpleName}Marshaller());"""
    }        

    out << """
    	}
    }
}
"""
    }
}
 	