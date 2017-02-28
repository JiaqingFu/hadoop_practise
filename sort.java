import java.io.IOException;
import java.util.StringTokenizer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;
//import com.kenai.jaffl.struct.Struct;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
/**
 * Created by fujiaqing on 2016/5/15 0015.
 */
public class sort {
    //�̳�mapper�ӿڣ�����map����������Ϊ<Object,Text>
    //�������Ϊ<Text,IntWritable>
    public static class Map extends Mapper<Object,Text,Text,Text>{
        //one��ʾ���ʳ���һ��
        private static IntWritable one = new IntWritable(1);
        //word�洢���µĵ���
        private Text word1= new Text();
        private Text word2 = new Text();
        public void map(Object key,Text value,Context context) throws IOException,InterruptedException{
            //����������д�
            String str = value.toString();
            String[] res=str.split("\t");
            if (res.length<7){return;}
            String u_s=res[1]+"\t"+res[4];
            String u_d=res[5]+"\t"+res[6]+"\t"+"1";
            word1.set(u_s);
            word2.set(u_d);
            context.write(word1, word2);
            //char[] ch = str.toCharArray();
//            for(int i = 0; i < ch.length ; i++){
//                if(ch[i] <= 126 && ch[i] >= 33){
//                    word.set(String.valueOf(ch[i]));
//                    context.write(word, one);
//                }
//            }
//            StringTokenizer st = new StringTokenizer(value.toString());
//            while(st.hasMoreTokens()){
//                word.set(st.nextToken());//���µĵ��ʴ���word
//                context.write(word, one);
//            }
        }
    }
    //�̳�reducer�ӿڣ�����reduce����������<Text,IntWritable>
    //�������Ϊ<Text,IntWritable>
    public static class Reduce extends Reducer<Text,Text,Text,Text>{
        //result��¼���ʵ�Ƶ��
        private Text keyline=new Text();
        private Text valueline=new Text();
        private static int line=0;
        //private Pattern pat=Pattern.compile("\\t");
        // private static Text text2 = new Text();
        public void reduce(Text key,Iterable<Text> values,Context context) throws IOException,InterruptedException{

            ArrayList<String> arr=new ArrayList<String>();
            for(Text val:values){
                arr.add(val.toString());
            }
            Collections.sort(arr, new Comparator<String>(){

                @Override
                public int compare(String arg0, String arg1) {
                    // TODO Auto-generated method stub
                    //String[] str1=pat.split(arg0);
                    //String[] str2=pat.split(arg1);
                    String[] str1=arg0.split("\t");
                    String[] str2=arg1.split("\t");
                    int tra1=Integer.parseInt(str1[0]);
                    int tra2=Integer.parseInt(str2[0]);
                    return tra1-tra2;
                }
            });
            for(String line:arr){
                //String[] lines=line.split("\t");
                keyline=key;
                valueline.set(line);
                context.write(keyline, valueline);
            }

            //�Ի�ȡ��<key,value-list>����value�ĺ�
//            try{
//                for(Text val:values){
//                    String[] dat=val.toString().split("\t");
//                    up+=Long.parseLong(dat[0]);
//                    down+=Long.parseLong(dat[1]);
//                    count+=Long.parseLong(dat[2]);
//                    //sum += val.get();
//                } }
//            catch (Exception e){}
            //��Ƶ�����õ�result
           // text1.set(String.valueOf(count)+"\t"+String.valueOf(up)+"\t"+String.valueOf(down));

            //�ռ����
           // context.write(key, text1);
        }
    }
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception{
        // TODO Auto-generated method stub
        Configuration conf = new Configuration();
        //conf.set("fs.defaultFS", "hdfs://192.168.83.30:8020");
        //conf.set("hadoop.job.user", "fujiaqing");
        //�����������
        //String[] args={"C:\\access.log","C:\\output"};
        String[] otherArgs = new GenericOptionsParser(conf,args).getRemainingArgs();
        if(otherArgs.length != 2){
            System.err.println("Usage WordCount <int> <out>");
            System.exit(2);
        }
        //������ҵ��
        Job job = new Job(conf,"sp count");
        //������ҵ������
        job.setJarByClass(spCount.class);
        job.setMapperClass(Map.class);
        job.setCombinerClass(Reduce.class);
        job.setReducerClass(Reduce.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

