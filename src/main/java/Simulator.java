import java.sql.Timestamp;
import java.util.*;

/**
 * Created by angelchambi on 3/7/16.
 * Simulator Purchase in Store
 */
public class Simulator{


    public static void Simulate(List<Map<String, Object>> objCustomerThings,
                                List<Map<String, Object>> objZones,
                                String host,
                                String port,
                                double shippingProb,
                                double purchaseProb,
                                double fittingRoomProb,
                                int zoneMovMax,
                                int minTimeInZoneSec,
                                int maxTimeInZoneSec,
                                String zoneExitCode,
                                String Fitting1,
                                String Fitting2,
                                int simulationHours,
                                int clientIntervalSec){
        try{
            //***************************BEGIN SIMULATION*******************************
            int minClientInterval = ((simulationHours * 3600) / objCustomerThings.size()) - clientIntervalSec;
            int maxClientInterval = ((simulationHours * 3600) / objCustomerThings.size()) + clientIntervalSec;

            for(int i = 0; i < objCustomerThings.size(); i++){

                //get Customer, Customer Thing Type and Group
                Map<String, Object> thingCustomer = objCustomerThings.get(i);
                Map thingTypeCustomer = (Map)thingCustomer.get("thingType");
                String groupCustomerHierarchyName = ((Map)thingCustomer.get("group")).get("hierarchyName").toString();

                //set Random moves between zones with MAX NUMBER and MIN NUMBER.
                int randomMoves = (int)(Math.random() * zoneMovMax);
                Calendar calendar = Calendar.getInstance();

                ArrayList<Map<String, Object>> listShippingProducts = new ArrayList<>();

                for(int j = 1; j <= randomMoves; j++){

                    //get random zone
                    //Posible error:  REPEATED ZONE, it must have a control to random number  in zones.
                    Map<String, Object> zoneRandom = objZones.get((int)(Math.random() * objZones.size()));

                    //Set random Time in zone with min time in zone and max time in zone.
                    String timePush = timeRandomIncrement(calendar, minTimeInZoneSec, maxTimeInZoneSec);

                    UpdateClientZone(host,
                                     port,
                                     groupCustomerHierarchyName,
                                     thingCustomer.get("name").toString(),
                                     thingCustomer.get("serial").toString(),
                                     thingTypeCustomer.get("thingTypeCode").toString(),
                                     zoneRandom.get("code").toString(),
                                     thingCustomer.get("id").toString(),
                                     timePush,
                                     listShippingProducts);

                    // Get All Things in zone where the customer was moved.
                    // Services not Found End point "Thing" to children.zone.value.name
                    // it used "Things" end point.
                    Map productInZone = Sender.getSomething("http://"
                                                            + host
                                                            + ":"
                                                            + port
                                                            + ""
                                                            + "/riot-core-services/api/things/?"
                                                            + "pageSize=-1&"
                                                            + "where=children.zone.value.name%3D"
                                                            + zoneRandom.get("name").toString().replace(" ", "%20")
                                                            + "%26Status.value%3C%3ESold&treeView=false"
                                                            + "&extra=thingType%2Cgroup");

                    List<Map<String, Object>> listProductInZone = (List)productInZone.get("results");


                    if (listProductInZone.size() > 0) {

                        System.out.println("The things in zone where the customer was moved are: "
                                           + listProductInZone.size());

                        int indexRandomProduct = (int)(Math.random() * listProductInZone.size());
                        Map randomProduct = listProductInZone.get(indexRandomProduct);
                        String prodGroupHierarchyName = Sender.groupThing(randomProduct.get("groupId").toString(),
                                                                          host,
                                                                          port);
                        ///Shipping Probability
                        if (shippingProb + Math.random() > 1) {
                            try{
                                System.out.println("The product choice in Zone: " + randomProduct);

                                Map message = AssociateProdWithCustomer(prodGroupHierarchyName,
                                                                        randomProduct.get("name").toString(),
                                                                        randomProduct.get("serialNumber").toString(),
                                                                        randomProduct.get("thingTypeCode").toString(),
                                                                        timePush,
                                                                        thingCustomer.get("serial").toString());


                                randomProduct.put("hierarchyName", prodGroupHierarchyName);

                                listShippingProducts.add(randomProduct);

                                Sender.patchSomething("http://"
                                                      + host
                                                      + ":"
                                                      + port
                                                      + "/riot-core-services/api/thing/"
                                                      + randomProduct.get("_id").toString(), message);

                            }
                            catch(Exception ex){
                                ex.printStackTrace();
                            }
                        }

                    }

                }

                // probability to go to fitting room
                if (fittingRoomProb + Math.random() > 1 && listShippingProducts.size() != 0) {
                    String fitting = ((int)(Math.random() * 3) == 2)?Fitting1:Fitting2;

                    UpdateClientZone(host,
                                     port,
                                     groupCustomerHierarchyName,
                                     thingCustomer.get("name").toString(),
                                     thingCustomer.get("serial").toString(),
                                     thingTypeCustomer.get("thingTypeCode").toString(),
                                     fitting,
                                     thingCustomer.get("id").toString(),
                                     timeRandomIncrement(calendar, minTimeInZoneSec, maxTimeInZoneSec),
                                     listShippingProducts);

                }

                purchaseProductProv(host,
                                    port,
                                    zoneExitCode,
                                    timeRandomIncrement(calendar, minTimeInZoneSec, maxTimeInZoneSec),
                                    purchaseProb,
                                    listShippingProducts,
                                    groupCustomerHierarchyName,
                                    thingCustomer,
                                    thingTypeCustomer);

                timeRandomIncrement(calendar, minClientInterval, maxClientInterval);
            }
        }
        catch(Exception ex){
            System.out.println("ERROR: " + ex);
        }
    }

    public static void purchaseProductProv(String host,
                                           String port,
                                           String zoneExitCode,
                                           String timePush,
                                           Double purchaseProb,
                                           List<Map<String, Object>> listProductsPurchased,
                                           String groupCustomerHierarchyName,
                                           Map thingCustomer,
                                           Map thingTypeCustomer){
        try{
            for(int i = 0; i < listProductsPurchased.size(); i++){
                int purchased = (int)(purchaseProb + Math.random());
                if (purchased == 1) {
                    Map product = (Map)listProductsPurchased.get(i);
                    ChangeZoneProduct(host, port, zoneExitCode, timePush, product);
                    changeSoldProduct(host, port, product, timePush);
                }
            }
            Sender.modifyZone(host,
                              port,
                              groupCustomerHierarchyName,
                              thingCustomer.get("name").toString(),
                              thingCustomer.get("serial").toString(),
                              thingTypeCustomer.get("thingTypeCode").toString(),
                              zoneExitCode,
                              thingCustomer.get("id").toString(),
                              timePush);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void changeSoldProduct(String host, String port, Map product, String timePush){
        Sender.modifyUdfString(host,
                               port,
                               product.get("hierarchyName").toString(),
                               product.get("serialNumber").toString(),
                               product.get("thingTypeCode").toString(),
                               "Sold",
                               product.get("_id").toString(),
                               timePush,
                               "Status");
    }

    public static void ChangeZoneProduct(String host, String port, String zoneRandomCode, String timePush, Map product){
        List<Map> listProductRFID = (List<Map>)product.get("children");
        for(int j = 0; j < listProductRFID.size(); j++){
            Map rfidItem = listProductRFID.get(j);
            Sender.modifyZone(host,
                              port,
                              product.get("hierarchyName").toString(),
                              rfidItem.get("name").toString(),
                              rfidItem.get("serialNumber").toString(),
                              rfidItem.get("thingTypeCode").toString(),
                              zoneRandomCode,
                              rfidItem.get("_id").toString(),
                              timePush);
        }
    }

    public static Map AssociateProdWithCustomer(String productGroup,
                                                String productName,
                                                String productSerialNumber,
                                                String productThingType,
                                                String timePush,
                                                String thingCustomerSerial){
        Map message = new HashMap<>();
        Map messageDetail = new HashMap<>();
        Map messageDetailCustomer = new HashMap<>();

        //Add Thing Type Field Customer to Product Thing.
        messageDetailCustomer.put("value", thingCustomerSerial);
        messageDetailCustomer.put("time", timePush);

        messageDetail.put("Customers", messageDetailCustomer);

        message.put("group", productGroup);
        message.put("name", productName);
        message.put("serialNumber", productSerialNumber);
        message.put("thingTypeCode", productThingType);
        message.put("udfs", messageDetail);
        message.put("time", timePush);
        System.out.println("message" + message);
        return message;
    }

    public static void ChangeZoneProducts(List<Map<String, Object>> listProductsPurchased,
                                          String host,
                                          String port,
                                          String zoneRandomCode,
                                          String timePush){
        try{
            //Update zone of all purchased things
            for(int i = 0; i < listProductsPurchased.size(); i++){
                Map product = (Map)listProductsPurchased.get(i);
                ChangeZoneProduct(host, port, zoneRandomCode, timePush, product);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void UpdateClientZone(String host,
                                        String port,
                                        String groupHierarchyName,
                                        String thingCustomerName,
                                        String thingCustomerSerial,
                                        String custThingTypeCode,
                                        String zoneRandomCode,
                                        String thingCustomerId,
                                        String timePush,
                                        List<Map<String, Object>> listShippingProducts){
        try{
            //modificar la zona de todos los productos asociados al customer
            Sender.modifyZone(host,
                              port,
                              groupHierarchyName,
                              thingCustomerName,
                              thingCustomerSerial,
                              custThingTypeCode,
                              zoneRandomCode,
                              thingCustomerId,
                              timePush);
            ChangeZoneProducts(listShippingProducts, host, port, zoneRandomCode, timePush);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static String timeRandomIncrement(Calendar calendar, int minTimeInZoneSec, int maxTimeInZoneSec){
        int randomTime = minTimeInZoneSec + (int)(Math.random() * maxTimeInZoneSec);
        calendar.add(Calendar.SECOND, randomTime);
        return String.valueOf(calendar.getTime().getTime());
    }
}
