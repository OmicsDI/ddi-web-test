import model.Database;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.lang.Math.abs;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by azorin on 29/08/2017.
 */
class DatabaseTest {

    public int i = 0;

    @BeforeAll
    static void collectStats(){
        TestRunner.loadSearchMetadata(); //totalCount and domains from EBI search
        TestRunner.loadSearchResults(); //sources from EBI search
        TestRunner.loadDatasetDatabases(); //datasetDatabases from mongo - dataset._id
        TestRunner.loadDatasetRepositories(); //repositories from mongo dataset.additional.repository
        TestRunner.loadDatabases(); //databases from mongo
    }

    @Test
    @DisplayName("must be more then 14 databases")
    void test1reconscile() {
        TestRunner.reconscile();
        assert(TestRunner.databases.size() >= 14);
    }


    @Test
    @DisplayName("must be no more then 10% differences in counts")
    void test2difference() {
        for(Database database : TestRunner.databases){
            try {

                int databases = TestRunner.datasetDatabases.get(database.database);
                int sources = TestRunner.sources.get(database.source);
                int repositories = TestRunner.repositories.get(database.repository);
                int domains = TestRunner.domains.get(database.domain);

                int avg = (databases + sources + repositories + domains) / 4;

                System.out.println("");

                assert (abs((databases - avg) / avg) < 0.1);
                assert (abs((sources - avg) / avg) < 0.1);
                assert (abs((repositories - avg) / avg) < 0.1);
                assert (abs((domains - avg) / avg) < 0.1);
            }
            catch(Exception ex){
                assertEquals(1,0,"EXCEPTION in"+ database.database + ":"+ex.getMessage());
            }
        }
    }
}