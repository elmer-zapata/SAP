import com.tierconnect.riot.simulator.utils.MatrixMath;
import com.tierconnect.riot.simulator.utils.XlsReader;

import org.apache.commons.math3.linear.RealMatrix;

import java.util.*;

/**
 * Created by angelchambi on 3/7/16. Simulator Purchase in Store
 */
public class Simulator {


    public static void simulate(List<Map<String, Object>> objCustomerThings,
                                String host,
                                String port,
                                double chanceTakeProd,
                                double chanceSeeProdInZone,
                                double purchaseProb,
                                double fittingRoomProb,
                                int zoneMovMax,
                                int minTimeInZoneSec,
                                int maxTimeInZoneSec,
                                String pathField,
                                String fileName,
                                String xlsSheet,
                                String zoneInCode,
                                String zoneExitCode,
                                String Fitting1,
                                String Fitting2,
                                int simulationHours,
                                int clientIntervalSec) {
        try {
            //***************************BEGIN SIMULATION*******************************
            int minClientInterval = ((simulationHours * 3600) / objCustomerThings.size()) - clientIntervalSec;
            int maxClientInterval = ((simulationHours * 3600) / objCustomerThings.size()) + clientIntervalSec;

            XlsReader xlsReader = new XlsReader(pathField, fileName);
            List<List<String>> resultGraphMatrix = xlsReader.loadXls(xlsSheet);
            RealMatrix realMatrix = MatrixMath.createTransitionVector(MatrixMath.convertMatrixReal(resultGraphMatrix));

            for (Map<String, Object> thingCustomer : objCustomerThings) {

                //get Customer, Customer Thing Type and Group
                Map thingTypeCustomer = (Map) thingCustomer.get("thingType");
                String groupCustomerHierarchyName = ((Map) thingCustomer.get("group")).get("hierarchyName").toString();


                Calendar calendar = Calendar.getInstance();
                String timePush = timeRandomIncrement(calendar, minTimeInZoneSec, maxTimeInZoneSec);

                //mode all customers on entrance zone
                Sender.modifyZone(host,
                        port,
                        groupCustomerHierarchyName,
                        thingCustomer.get("name").toString(),
                        thingCustomer.get("serial").toString(),
                        thingTypeCustomer.get("thingTypeCode").toString(),
                        zoneInCode,
                        thingCustomer.get("id").toString(),
                        timePush);

                List<Map<String, Object>> takenProdList = new ArrayList<>();

                String nextZoneName = zoneInCode.replace(".", " ");
                String nextZoneCode = zoneInCode;

                int stepNumber = 0;

                while (!nextZoneCode.equals(zoneExitCode) && stepNumber <= zoneMovMax) {

                    //get random zone
                    double[] zoneSteps = realMatrix.getRow(resultGraphMatrix.get(0).indexOf(nextZoneName) - 1);
                    int netZoneIndex = MatrixMath.getRandomIndex(zoneSteps) + 1;
                    nextZoneName = resultGraphMatrix.get(0).get(netZoneIndex);
                    nextZoneCode = String.join(".", resultGraphMatrix.get(0).get(netZoneIndex).split(" "));

                    //Set random Time in zone with min time in zone and max time in zone.
                    timePush = timeRandomIncrement(calendar, minTimeInZoneSec, maxTimeInZoneSec);

                    UpdateClientZone(host,
                            port,
                            groupCustomerHierarchyName,
                            thingCustomer.get("name").toString(),
                            thingCustomer.get("serial").toString(),
                            thingTypeCustomer.get("thingTypeCode").toString(),
                            nextZoneCode,
                            thingCustomer.get("id").toString(),
                            timePush,
                            takenProdList);

                    int seeProducts = (int) (chanceSeeProdInZone + Math.random());
                    if (seeProducts != 1) {
                        continue;
                    }
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
                            + "where=children.zone.value.code%3D"
                            + nextZoneCode.replace(" ", "%20")
                            + "%26Status.value%3C%3ESold&treeView=false"
                            + "&extra=thingType%2Cgroup");

                    List<Map<String, Object>>
                            listProductInZone
                            = (List<Map<String, Object>>) productInZone.get("results");
                    takenProdList = takeProductsInZone(listProductInZone,
                            chanceTakeProd,
                            host,
                            port,
                            timePush,
                            thingCustomer.get("serial").toString(),
                            takenProdList);
                    stepNumber++;
                }


                // probability to go to fitting room
                if (fittingRoomProb + Math.random() > 1 && takenProdList.size() != 0) {
                    String fitting = ((int) (Math.random() * 3) == 2) ? Fitting1 : Fitting2;

                    UpdateClientZone(host,
                            port,
                            groupCustomerHierarchyName,
                            thingCustomer.get("name").toString(),
                            thingCustomer.get("serial").toString(),
                            thingTypeCustomer.get("thingTypeCode").toString(),
                            fitting,
                            thingCustomer.get("id").toString(),
                            timeRandomIncrement(calendar, minTimeInZoneSec, maxTimeInZoneSec),
                            takenProdList);

                }

                purchaseProductProv(host,
                        port,
                        zoneExitCode,
                        timeRandomIncrement(calendar, minTimeInZoneSec, maxTimeInZoneSec),
                        purchaseProb,
                        takenProdList,
                        groupCustomerHierarchyName,
                        thingCustomer,
                        thingTypeCustomer);

                timeRandomIncrement(calendar, minClientInterval, maxClientInterval);
            }
        } catch (Exception ex) {
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
                                           Map<String, Object> thingCustomer,
                                           Map thingTypeCustomer) {
        try {
            for (Map<String, Object> product : listProductsPurchased) {
                int purchased = (int) (purchaseProb + Math.random());
                if (purchased == 1) {
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void changeSoldProduct(String host, String port, Map<String, Object> product, String timePush) {
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

    public static void ChangeZoneProduct(String host, String port, String zoneRandomCode, String timePush, Map product) {
        List<Map> listProductRFID = (List<Map>) product.get("children");
        for (Map rfidItem : listProductRFID) {
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

    public static Map<Object, Object> createHashMapProdWithCustomer(String productGroup,
                                                                    String productName,
                                                                    String productSerialNumber,
                                                                    String productThingType,
                                                                    String timePush,
                                                                    String thingCustomerSerial) {
        Map<Object, Object> message = new HashMap<>();
        Map<Object, Object> messageDetail = new HashMap<>();
        Map<Object, Object> messageDetailCustomer = new HashMap<>();

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
                                          String timePush) {
        try {
            //Update zone of all purchased things
            for (Map<String, Object> aListProductsPurchased : listProductsPurchased) {
                ChangeZoneProduct(host, port, zoneRandomCode, timePush, aListProductsPurchased);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static List<Map<String, Object>> takeProductsInZone(List<Map<String, Object>> listProductInZone,
                                                               double chanceTakeProd,
                                                               String host,
                                                               String port,
                                                               String timePush,
                                                               String thingCustomerSerial,
                                                               List<Map<String, Object>> takenProdList) {
        if (listProductInZone.size() > 0) {


            System.out.println("The things in zone where the customer was moved are: " + listProductInZone.size());

            for (Map<String, Object> productItem : listProductInZone) {

                // Probability to Take a product
                int getProd = (int) (chanceTakeProd + Math.random());
                if (getProd != 1) {
                    continue;
                }

                try {
                    String prodGroupHierarchyName = Sender.groupThing(productItem.get("groupId").toString(),
                            host,
                            port);
                    System.out.println("The product choice in Zone: " + productItem.get("name"));
                    Map<Object, Object> message = createHashMapProdWithCustomer(prodGroupHierarchyName,
                            productItem.get("name").toString(),
                            productItem.get("serialNumber")
                                    .toString(),
                            productItem.get("thingTypeCode")
                                    .toString(),
                            timePush,
                            thingCustomerSerial);

                    productItem.put("hierarchyName", prodGroupHierarchyName);

                    Sender.patchSomething("http://" + host + ":" + port + "/riot-core-services/api/thing/" + productItem
                            .get("_id")
                            .toString(), message);
                    takenProdList.add(productItem);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return takenProdList;

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
                                        List<Map<String, Object>> listShippingProducts) {
        try {
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String timeRandomIncrement(Calendar calendar, int minTimeInZoneSec, int maxTimeInZoneSec) {
        int randomTime = minTimeInZoneSec + (int) (Math.random() * maxTimeInZoneSec);
        calendar.add(Calendar.SECOND, randomTime);
        return String.valueOf(calendar.getTime().getTime());
    }
}
