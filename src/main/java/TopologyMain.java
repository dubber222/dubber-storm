import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import bolts.WordCounter;
import bolts.WordNormalizer;
import spouts.WordReader;


public class TopologyMain {
    public static void main(String[] args) throws InterruptedException {

        //Topology definition
        //�������ˣ�������Storm��ΰ��Ÿ��ڵ㣬�Լ����ǽ������ݵķ�ʽ��
        TopologyBuilder builder = new TopologyBuilder();

        /**
         * ��spout��bolts֮��ͨ�� shuffleGrouping ��������
         * shuffleGrouping
         *  ���ַ��鷽ʽ������Storm����������䷽ʽ��Դ�ڵ���Ŀ��ڵ㷢����Ϣ��
         */
        builder.setSpout("word-reader", new WordReader());
        builder.setBolt("word-normalizer", new WordNormalizer())
                .shuffleGrouping("word-reader");

        /**
         * ��bolts��bolts֮��ͨ�� fieldsGrouping ��������
         * fieldsGrouping
         *  ���ַ��鷽ʽ������Storm���Թ̶��ֶη����һ���ڵ�����һ���ڵ㷢����Ϣ��
         *  parallelism_hint ����2 : 2�� wordCounter ��������
         */
        builder.setBolt("word-counter", new WordCounter(), 2)
                .fieldsGrouping("word-normalizer", new Fields("word"));

        //Configuration
        //��������
        Config conf = new Config();
        conf.put("wordsFile", args[0]);
        conf.setDebug(false);
        //Topology run
        conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("Getting-Started-Toplogie", conf, builder.createTopology());
        Thread.sleep(1000);
        cluster.shutdown();


    }
}
