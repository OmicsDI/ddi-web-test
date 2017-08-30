import com.mongodb.*;
import model.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.*;

/**
 * Created by azorin on 21/08/2017.
 */
public class TestRunner {
    public static void main(String[] args){
        loadSearchMetadata(); //totalCount and domains from EBI search
        loadSearchResults(); //sources from EBI search
        loadDatasetDatabases(); //datasetDatabases from mongo - dataset._id
        loadDatasetRepositories(); //repositories from mongo dataset.additional.repository
        loadDatabases(); //databases from mongo

        reconscile();
    }

    public static String getUrl(int pageSize, int start){
        String s =  String.format("http://www.ebi.ac.uk/ebisearch/ws/rest/omics?query=*:*&start=%s&size=%s&format=JSON",start,pageSize);
        return s;
    }

    static List<Database> databases = new ArrayList<Database>();
    static Map<String,Integer> domains = new HashMap<String,Integer>();
    static Map<String,Integer> sources = new HashMap<String,Integer>();
    static Map<String,Integer> datasetDatabases = new HashMap<String,Integer>();
    static Map<String,Integer> repositories = new HashMap<String,Integer>();
    static Integer totalCount = 0;

    public static void loadSearchMetadata(){
        RestTemplate template = new RestTemplate();
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        String url = "http://www.ebi.ac.uk/ebisearch/ws/rest/omics?query=*:*&start=0&size=10&format=JSON&facetcount=20";
        SearchResponse response = restTemplate.getForObject(url, SearchResponse.class);
        for(Facet facet : response.facets){
            if(facet.id.equals("domain_source")){
                for(FacetValue facetValue : facet.facetValues){
                    String domain = facetValue.label;
                    Integer count = facetValue.count;
                    domains.put(domain,count);
                }
            }
        }
        totalCount = response.hitCount;
        System.out.print("total:" + totalCount + "\n");
        for(String s: domains.keySet()){
            System.out.print("domain:"+ s+" : "+domains.get(s)+"\n");
        }
    }

    public static void loadSearchResults(){
        RestTemplate template = new RestTemplate();
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        int pageSize = 100;
        for(int i = 0; i!= totalCount/pageSize; i++){
            String url = getUrl(pageSize,i*pageSize);
            //System.out.print("calling:" + i*pageSize + " " + url + "\n");
            System.out.print(".");
            for(Entry e : restTemplate.getForObject(url, SearchResponse.class).entries)
            {
                String source = e.source;
                Integer count = sources.get(source);

                if (count == null) {
                    sources.put(source, 1);
                    System.out.print(source);
                }
                else {
                    sources.put(source, count + 1);
                }
            }
        }
        for(String key : sources.keySet()) {
            System.out.print("source from EBI:"+key +":" + sources.get(key) + "\n");
        }
    }

    private static MongoClient getMongoClient(){
        Properties properties = new Properties();

        try (InputStream is = TestRunner.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(is);
        }catch(Exception ex){
            System.out.print("...EXCEPTION...:"+ex.getMessage());
        }

        final String mongoServer = properties.getProperty("ddi.mongo.machine.one");
        System.out.print("mongo server" + mongoServer);

        final Integer mongoPort = Integer.parseInt(properties.getProperty("ddi.mongo.port"));
        final String mongoUser = properties.getProperty("ddi.mongo.user");
        final String mongoPassword = properties.getProperty("ddi.mongo.password");

        ServerAddress serverAddress = new ServerAddress(mongoServer , mongoPort );

        MongoCredential credential = MongoCredential.createCredential(mongoUser, "admin", mongoPassword.toCharArray());

        MongoClient mongoClient = new MongoClient(serverAddress, Arrays.asList(credential));

        return mongoClient;
    }

    public static void loadDatasetDatabases(){
        MongoClient mongoClient = getMongoClient();

        DB db = mongoClient.getDB( "ddi_db" );

        List<DBObject> agg = new ArrayList<DBObject>();

        DBObject o = new BasicDBObject();

        DBObject o1 = new BasicDBObject();

        o1.put("_id","$database");//{_id:, count:{$sum:1}}

        o1.put("count",new BasicDBObject("$sum", 1));

        o.put("$group",o1);

        agg.add(o);

        Iterable<DBObject> output = db.getCollection("datasets.dataset").aggregate(agg).results();

        for (DBObject o4 : output) {
            datasetDatabases.put(o4.get("_id").toString(),Integer.parseInt(o4.get("count").toString()));
            System.out.print("dataset.database:"+o4.get("_id").toString() + " " + o4.get("count").toString() + "\n");
        }
        mongoClient.close();
    }

    public static void loadDatasetRepositories(){

        MongoClient mongoClient = getMongoClient();

        DB db = mongoClient.getDB( "ddi_db" );

        List<DBObject> agg = new ArrayList<DBObject>();

        DBObject o = new BasicDBObject();

        DBObject o1 = new BasicDBObject();

        o1.put("_id","$additional.repository");//{_id:, count:{$sum:1}}

        o1.put("count",new BasicDBObject("$sum", 1));

        o.put("$group",o1);

        agg.add(o);

        Iterable<DBObject> output = db.getCollection("datasets.dataset").aggregate(agg).results();

        for (DBObject o4 : output) {
            BasicDBList id_list = (BasicDBList) o4.get("_id");
            String id = id_list.get(0).toString(); //assume one and only one repository
            Integer count = Integer.parseInt(o4.get("count").toString());
            repositories.put(id,count);
            System.out.print("additional.repository:"+id + " " + count + "\n");
        }
        mongoClient.close();
    }

    public static void loadDatabases(){

        MongoClient mongoClient = getMongoClient();

        DB db = mongoClient.getDB( "ddi_db" );

        DBCursor output = db.getCollection("databases").find();
        while(output.hasNext()){
            DBObject o = output.next();
            Database database = new Database();
            database.repository = o.get("repository").toString();
            database.source =  o.get("source").toString();
            database.database =  o.get("_id").toString();
            database.domain =  o.get("domain").toString();
            databases.add(database);
        }
        mongoClient.close();

        for (Database database: databases) {
            System.out.print("database:"+database.database+" repository:"+database.repository+"\n");
        }
    }

    public static void reconscile(){
        System.out.print("database,source,repository,domain,N_database,N_source,N_repository,N_domain"+ "\n");
        for(Database database: databases){
            System.out.print(database.database+",");
            System.out.print(database.source+",");
            System.out.print(database.repository+",");
            System.out.print(database.domain+",");

            System.out.print(datasetDatabases.get(database.database)+","); // "\n");
            System.out.print(sources.get(database.source)+",");
            System.out.print(repositories.get(database.repository)+",");
            System.out.print(domains.get(database.domain)+"\n");
        }
    }

}
