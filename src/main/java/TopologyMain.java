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
        //创建拓扑，它决定Storm如何安排各节点，以及它们交换数据的方式。
        TopologyBuilder builder = new TopologyBuilder();

        /**
         * 在spout和bolts之间通过 shuffleGrouping 方法连接
         * shuffleGrouping
         *  这种分组方式决定了Storm会以随机分配方式从源节点向目标节点发送消息）
         */
        builder.setSpout("word-reader", new WordReader());
        builder.setBolt("word-normalizer", new WordNormalizer())
                .shuffleGrouping("word-reader");

        /**
         * 在bolts和bolts之间通过 fieldsGrouping 方法连接
         * fieldsGrouping
         *  这种分组方式决定了Storm会以固定字段分组从一个节点向另一个节点发送消息）
         *  parallelism_hint 设置2 : 2个 wordCounter 并行运行
         */
        builder.setBolt("word-counter", new WordCounter(), 2)
                .fieldsGrouping("word-normalizer", new Fields("word"));

        //Configuration
        //拓扑配置
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
