/**
 * 
 * Copyright 2005-2006 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
/**
 * Optimized writes to a RandomAcessFile
 * 
 * @version $Revision: 1.1.1.1 $
 */
package org.apache.activemq.kaha.impl;

import java.io.IOException;
import java.io.RandomAccessFile;
/**
 * Optimized Store writer
 * 
 * @version $Revision: 1.1.1.1 $
 */
class StoreIndexWriter{
    
    protected final StoreByteArrayOutputStream dataOut = new StoreByteArrayOutputStream();
    protected final RandomAccessFile file;
    protected final String name;
    protected final DataManager redoLog;

    /**
     * Construct a Store index writer
     * 
     * @param file
     */
    StoreIndexWriter(RandomAccessFile file){
        this(file, null, null);
    }

    public StoreIndexWriter(RandomAccessFile file, String indexName, DataManager redoLog) {
        this.file=file;
        this.name = indexName;
        this.redoLog = redoLog;
    }

    void storeItem(IndexItem indexItem) throws IOException{
        
        if( redoLog!=null ) {
            RedoStoreIndexItem redo = new RedoStoreIndexItem(name, indexItem.getOffset(), indexItem);
            redoLog.storeRedoItem(redo);
        }
        
        dataOut.reset();
        indexItem.write(dataOut);
        file.seek(indexItem.getOffset());
        file.write(dataOut.getData(),0,IndexItem.INDEX_SIZE);
    }

    public void redoStoreItem(RedoStoreIndexItem redo) throws IOException {
        dataOut.reset();
        redo.getIndexItem().write(dataOut);
        file.seek(redo.getOffset());
        file.write(dataOut.getData(),0,IndexItem.INDEX_SIZE);
    }
    
}
