# FlatbuffersSample
flatbuffers的简单使用

1.集成FlatBuffer的java源码https://github.com/google/flatbuffers /trunk/java目录

2.https://github.com/google/flatbuffers/releases下载flatc.exe

3.编写fbs文件

4.flatc.exe --java xx.fbs生成java文件

5. 示例: 详情请查阅代码

   ```Java
   A {
    
        int aa,
       
        String bb,
       
         List<ItemObj> ccList
       
    }

    ItemObj {
    
          int shuzi = 100;
      
          String aStr;
      
        String bStr;
      
   }
  
  //添加数组
  
    int[] dictOffsets = new int[num];
  
    for (int n = 0; n < num; n++) {
  
       int shuzi = 100;
     
       int kOffset = fbb.createString(aStr);
     
       int nOffset = fbb.createString(bStr);
     
       dictOffsets[n] = ItemObj.createItem(fbb,kOffset,nOffset,shuzi);
     
     }
     
    int listOffset = A.createListVector(fbb,dictOffsets);
  
    A.startStockDictList(fbb);
  
    A.addAa(fbb, aa);
  
    int bbOffset = fbb.createString(bb);
  
    A.addBb(fbb, bbOffset);
  
    A.addCcList(fbb,listOffset);
  
    int endOffset = StockDictList.endStockDictList(fbb);
  
     or

    int endOffset = A.createListVector(fbb,aa,bbOffset,listOffset);
  
    fbb.finish(endOffset);
  
    Bytebuf bytebuf = fbb.dataBuffer();
    ```
