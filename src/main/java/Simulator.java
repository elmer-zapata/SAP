import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by angelchambi on 3/7/16.
 */
public class Simulator{


    public static void Simulate(int num_records,
                                List<Map<String, Object>> objThings,
                                List<Map<String, Object>> objZones,
                                String host,
                                String port,
                                double probability,
                                String Fitting1,
                                String Fitting2){
        long current = System.currentTimeMillis();
        try{
            //***************************BEGIN SIMULATION*******************************

            for(int i = 0; i < num_records; i++){
                //necesito el thing para moverlo
                Map<String, Object> thingForProced = objThings.get(i);
                Map<String, Object> thingTypeforProced = (Map)thingForProced.get("thingType");
                Map<String, Object> groupForthing = (Map)thingForProced.get("group");

                //movimiento entre zonas
                int randomMoves = (int)(Math.random() * 9);
                Timestamp stamp = new Timestamp(current);
                java.sql.Date date = new java.sql.Date(stamp.getTime());
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                //hacer el primer movimiento a la zona o zonas de entrada
                String timeForTheLast = "";
                for(int j = 1; j <= randomMoves; j++){
                    int randomZone = (int)(Math.random() * objZones.size());
                    //sacar una zona a la suerte

                    Map<String, Object> es = objZones.get(randomZone);
                    //cambiar el random de tiempo
                    int randomTime = 10 + (int)(Math.random() * 3600);
                    cal.add(Calendar.MINUTE, randomTime);
                    java.sql.Date date2 = new java.sql.Date(cal.getTime().getTime());

                    //logger.info(date2);
                    long initialTime = date2.getTime();
                    //aqui hacer el update del thing si es q la probabilidad nos deja
                    String timePush = String.valueOf(initialTime);
                    timeForTheLast = timePush;
                    //get para calcular los things dentro de la zona q se movio el customer
                    Map<String, Object> thingsInZones = Sender.getSomething("http://"
                                                                            + host
                                                                            + ":"
                                                                            + port
                                                                            + ""
                                                                            + "/riot-core-services/api/things/?pageSize=-1&where=children.zone.value.name%3D"
                                                                            + es.get("name")
                                                                                .toString()
                                                                                .replace(" ", "%20")
                                                                            + "%26Status.value%3C%3ESold&treeView=false");

                    System.out.println("el tamaño de thingsInzone" + thingsInZones.size());

                    List<Map<String, Object>> listThings = (List)thingsInZones.get("results");
                    System.out.println("el tamaño de things in zone" + listThings.size());
                    if (listThings.size() > 0) {
                        System.out.println("entroasdfsdfasffghjk");
                        int thingInZoneRandom = (int)(Math.random() * listThings.size());
                        //Map<String,Object>children=(Map)listThings.get(thingInZoneRandom).get("children");
                        String thingTypeChildren = listThings.get(thingInZoneRandom).get("thingTypeCode").toString();
                        String nameChild = listThings.get(thingInZoneRandom).get("name").toString();
                        // String serialChild=listThings.get(thingInZoneRandom).get("serialNumber").toString();

                        // System.out.print(listThings.get(thingInZoneRandom).get("groupId"));

                        String groupthing = Sender.groupThing(listThings.get(thingInZoneRandom)
                                                                        .get("groupId")
                                                                        .toString(), host, port);
                        ///cambiar la formula de la probabilidad
                        if (probability + Math.random() > 1) {
                            try{
                                Map message = new HashMap<>();
                                Map messageDetail = new HashMap<>();
                                Map messageDetailCustomer = new HashMap<>();
                                message.put("group", groupthing);
                                message.put("name", nameChild);
                                message.put("serialNumber", nameChild.replace(" ", "."));
                                message.put("thingTypeCode", thingTypeChildren);
                                message.put("udfs", messageDetail);
                                messageDetail.put("Customers", messageDetailCustomer);
                                messageDetailCustomer.put("value",
                                                          thingForProced.get("name").toString().replace(" ", "."));
                                messageDetailCustomer.put("time", timePush);
                                System.out.println("message" + message);
                                System.out.println("message2" + listThings.get(thingInZoneRandom));
                                //System.out.println("patch"+"http://"+host + ":" + port + "/riot-core-services/api/thing/" + listThings.get(thingInZoneRandom).get("id").toString());
                                Sender.patchSomething("http://"
                                                      + host
                                                      + ":"
                                                      + port
                                                      + "/riot-core-services/api/thing/"
                                                      + listThings.get(thingInZoneRandom).get("_id").toString(),
                                                      message);
                                Sender.modifyZone(host,
                                                  port,
                                                  groupForthing.get("hierarchyName").toString(),
                                                  thingForProced.get("name").toString(),
                                                  thingTypeforProced.get("thingTypeCode").toString(),
                                                  es.get("code").toString(),
                                                  thingForProced.get("id").toString(),
                                                  timePush);
                                //modificar la zona de todos los productos asociados al customer
                            }
                            catch(Exception ex){
                                ex.printStackTrace();
                            }
                        }
                        else {
                            try{
                                //modificar la zona de todos los productos asociados al customer
                                Sender.modifyZone(host,
                                                  port,
                                                  groupForthing.get("hierarchyName").toString(),
                                                  thingForProced.get("name").toString(),
                                                  thingTypeforProced.get("thingTypeCode").toString(),
                                                  es.get("code").toString(),
                                                  thingForProced.get("id").toString(),
                                                  timePush);
                            }
                            catch(Exception ex){
                                ex.printStackTrace();
                            }
                        }

                    }
                    else {
                        Sender.modifyZone(host,
                                          port,
                                          groupForthing.get("hierarchyName").toString(),
                                          thingForProced.get("name").toString(),
                                          thingTypeforProced.get("thingTypeCode").toString(),
                                          es.get("code").toString(),
                                          thingForProced.get("id").toString(),
                                          timePush);
                    }
                }
                ///probabildad de q vaya a los vestidores y desacioar algunos
                if (probability + Math.random() > 1) {
                    Sender.modifyZone(host,
                                      port,
                                      groupForthing.get("hierarchyName").toString(),
                                      thingForProced.get("name").toString(),
                                      thingTypeforProced.get("thingTypeCode").toString(),
                                      ((int)(Math.random() * 3) == 2)?Fitting1:Fitting2,
                                      thingForProced.get("id").toString(),
                                      timeForTheLast);
                }


                int randomTime = (int)(Math.random() * 11);
                cal.add(Calendar.MINUTE, randomTime);
                java.sql.Date date2 = new java.sql.Date(cal.getTime().getTime());
                long initialTime = date2.getTime();
                timeForTheLast = String.valueOf(initialTime);

                if (Sender.haveProduct(host, thingForProced)) {
                    System.out.println("entro ");
                    List<Map<String, Object>> a = Sender.returnParent(host, thingForProced);
                    for(int k = 0; k < a.size(); k++){
                        System.out.println("entro a modificar el status");
                        Sender.modifyUdfString(host,
                                               port,
                                               groupForthing.get("hierarchyName").toString(),
                                               a.get(k).get("serialNumber").toString(),
                                               a.get(k).get("thingTypeCode").toString(),
                                               "Sold",
                                               a.get(k).get("_id").toString(),
                                               timeForTheLast,
                                               "Status");
                    }
                }

                Sender.modifyZone(host,
                                  port,
                                  groupForthing.get("hierarchyName").toString(),
                                  thingForProced.get("name").toString(),
                                  thingTypeforProced.get("thingTypeCode").toString(),
                                  "Main.Exit",
                                  thingForProced.get("id").toString(),
                                  timeForTheLast);


            }
        }
        catch(Exception ex){
            System.out.print("ERROR: " + ex);
        }
    }
}