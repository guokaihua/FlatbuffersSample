# FlatbuffersSample

#### flatbuffers的简单使用

##### 1.[集成FlatBuffer源码](https://github.com/google/flatbuffers) /trunk/java目录

##### 2.[下载flatc.exe](https://github.com/google/flatbuffers/releases)

##### 3.编写fbs文件

##### 4.flatc.exe --java xx.fbs生成java文件

##### 5.示例: 详情请查阅代码

   ```Java
   
   //fbs结构定义 xxx.fbs
   namespace 包路径;
   table A {
       aa : int;
       bb : string;
       ccList : [ItemObj];
   }
   
   table ItemObj {
       shuzi : int;
       aStr : string;
       bStr : string;
   }
   root_type A;
   //通过命令 flatc --java -o xxx.fbs生成 A.java ItemObj.java
   
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
   //或者
   int endOffset = A.createListVector(fbb,aa,bbOffset,listOffset);
   
   fbb.finish(endOffset);
   
   Bytebuf bytebuf = fbb.dataBuffer();
