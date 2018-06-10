package co.com.millennialapps.owlmapp.utilitites;

import android.app.Activity;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import co.com.millennialapps.owlmapp.R;
import co.com.millennialapps.owlmapp.models.Instruction;
import co.com.millennialapps.owlmapp.models.Node;
import co.com.millennialapps.utils.tools.MapHandler;

/**
 * Created by erick on 24/10/2017.
 */

public class Dijkstra {

    private final int LIMIT_FRONT = 45;
    private final int LIMIT_TURN_ON = 100;

    private final int FRONT = 0;
    private final int LEFT = 1;
    private final int RIGTH = 2;
    private final int TURN_ON = 3;
    private final int START = 7;
    private final int ADRIFT = 8;
    private final int NONE = 9;

    private final LinkedList<Node> shortestPath = new LinkedList<>();
    private final LinkedList<Instruction> instructions = new LinkedList<>();

    private HashMap<String, Node> nodeList = new HashMap<>();

    private final MapHandler mapHandler;

    public Dijkstra(MapHandler mapHandler) {
        this.mapHandler = mapHandler;
    }

    private static Dijkstra dijkstra;

    public static Dijkstra getInstance(MapHandler mapHandler) {
        return dijkstra == null ? dijkstra = new Dijkstra(mapHandler) : dijkstra;
    }

    public void findShortestPath(HashMap<String, Node> nodeList, Node from, Node to) {
        this.nodeList = nodeList;
        HashMap<String, Float> distances = new HashMap<>();
        HashMap<String, String> parents = new HashMap<>();
        HashMap<String, Node> nodes = new HashMap<>(nodeList);
        for (Map.Entry<String, Node> node : nodes.entrySet()) {
            distances.put(node.getKey(), Float.MAX_VALUE);
            parents.put(node.getKey(), "");
        }
        distances.put(from.getId(), (float) 0);
        Node currentNode;
        float minDistance;
        String minId;
        Node nodeLink;
        float d;

        while (!nodes.isEmpty()) {
            minDistance = Float.MAX_VALUE;
            minId = "";
            //Busca la menor distancia de todas
            for (Map.Entry<String, Float> distance : distances.entrySet()) {
                if (distance.getValue() < minDistance) {
                    minDistance = distance.getValue();
                    minId = distance.getKey();
                }
            }
            distances.remove(minId);
            currentNode = nodes.remove(minId);

            //Usa el nodo con la menor distancia
            if (currentNode != null) {
                if (currentNode.getId().equals("0hSaGlCJ5MQaq4ANN7iw")) {
                    int a = 1;
                }
                //Mira si ese nodo tiene enlaces con otros nodos
                if (currentNode.getLinks() != null) {
                    //Recorre todos los enlaces a los que tenga acceso y calcula sus distancias
                    for (String link : currentNode.getLinks()) {
                        if (link.equals("0hSaGlCJ5MQaq4ANN7iw")) {
                            int a = 1;
                        }
                        nodeLink = nodes.get(link);
                        if (nodeLink != null) {
                            if (!nodeLink.getType().equals("") || nodeLink.getId().equals(to.getId())) {
                                //Calcula la distancia del nodo actual a cada uno de los enlaces que tenga
                                d = mapHandler.distance(currentNode.getLatLng(), nodeLink.getLatLng(), MapHandler.METERS);
                                //Revisa si la distancia desde el inicio hasta ahí es menor por este enlace que por algún otro camino visto
                                if (d + minDistance < distances.get(link)) {
                                    parents.put(link, currentNode.getId());
                                    distances.put(link, d + minDistance);
                                }
                                if (minId.equals(to.getId())) {
                                    break;
                                }
                            }
                        }
                    }
                    if (minId.equals(to.getId())) {
                        break;
                    }
                }
            } else {
                break;
            }

        }

        generatePath(parents, to);
    }

    private void generatePath(HashMap<String, String> parents, Node from) {
        shortestPath.clear();
        Node childNode = from;
        String parent = "";
        do {
            parent = parents.get(childNode.getId());
            shortestPath.addFirst(childNode);
            childNode = this.nodeList.get(parent);
        } while (!parent.isEmpty());

        generateInstructions();
    }

    private void generateInstructions() {
        instructions.clear();

        Instruction instruction;
        int direction = -1;
        boolean isFront = false;
        boolean isAdrift = false;
        Node nodeFrom = null;
        Node prevNode = null;
        Node node = shortestPath.getFirst();
        double prevAngle = -1;
        double angle = 0;
        String name = "";
        String nameFrom = "";
        double angule = 0;

        instructions.add(new Instruction(node, R.drawable.ic_from_dark, "Empiezas en " + node.getName()));
        nodeFrom = node;
        nameFrom = node.getName();

        for (Node step : shortestPath) {
            node = step;
            if (prevNode != null) {
                //Cuando sale del primer nodo al segundo en adelante
                if (!node.isSearchDirection()) {
                    if (!isAdrift) {
                        isAdrift = true;
                        direction = ADRIFT;
                        name = getCloserNode(mapHandler, prevNode).getName();
                    } else {
                        direction = NONE;
                    }
                } else {
                    if (isAdrift) {
                        direction = ADRIFT;
                        isAdrift = false;
                        name = getCloserNode(mapHandler, node).getName();
                        angle = 0.0;
                    } else {
                        angle = mapHandler.direction(prevNode.getLatLng(), node.getLatLng());
                        if (prevAngle > 0.0) {
                            Node closerNode = getCloserNode(mapHandler, prevNode);
                            name = closerNode.getName();
                            if (name.equals("")) {
                                name = closerNode.getId();
                            }
                            angule = ((angle - prevAngle) + 360) % 360;
                            if (angule < LIMIT_FRONT || angule > 360 - LIMIT_FRONT) {
                                direction = FRONT;
                                isFront = true;
                            } else if (angule < LIMIT_FRONT + LIMIT_TURN_ON) {
                                direction = RIGTH;
                                isFront = false;
                                if (!nameFrom.equals(name)) {
                                    instruction = new Instruction(nodeFrom, step, R.drawable.ic_front,
                                            "Ve recto desde " + nameFrom + " hasta " + name);
                                    instructions.add(instruction);
                                }
                                instruction = new Instruction(nodeFrom, step, R.drawable.ic_right,
                                        "Gira a la derecha en " + name);
                                instructions.add(instruction);
                                nodeFrom = step;
                                nameFrom = name;
                            } else if (angule < 360 - (LIMIT_FRONT + LIMIT_TURN_ON)) {
                                isFront = false;
                                if (!nameFrom.equals(name)) {
                                    instruction = new Instruction(nodeFrom, step, R.drawable.ic_front,
                                            "Ve recto desde " + nameFrom + " hasta " + name);
                                    instructions.add(instruction);
                                }
                                instruction = new Instruction(nodeFrom, step, R.drawable.ic_turn_on,
                                        "Gira en " + name);
                                instructions.add(instruction);
                                nodeFrom = step;
                                nameFrom = name;
                                direction = TURN_ON;
                            } else {
                                direction = LEFT;
                                isFront = false;
                                if (!nameFrom.equals(name)) {
                                    instruction = new Instruction(nodeFrom, step, R.drawable.ic_front,
                                            "Ve recto desde " + nameFrom + " hasta " + name);
                                    instructions.add(instruction);
                                }
                                instruction = new Instruction(nodeFrom, step, R.drawable.ic_left,
                                        "Gira a la izquierda en " + name);
                                instructions.add(instruction);
                                nodeFrom = step;
                                nameFrom = name;
                            }
                        }
                    }
                    break;
                }

                switch (direction) {
                    case ADRIFT:
                        isFront = false;
                        if (!nameFrom.equals(name)) {
                            instruction = new Instruction(nodeFrom, step, R.drawable.ic_front,
                                    "Ve desde " + nameFrom + " hasta " + name);
                            instructions.add(instruction);
                        }
                        nameFrom = name;
                        nodeFrom = step;
                        break;
                    case NONE:
                        break;
                }

                prevAngle = angle;
            }
            prevNode = node;
        }
        if (isFront) {
            instruction = new Instruction(nodeFrom, node, R.drawable.ic_front,
                    "Ve recto desde " + nameFrom + " hasta " + node.getName());
            instructions.add(instruction);
        }
        instruction = new Instruction(node, R.drawable.ic_to_dark,
                "Llegaste a " + node.getName());
        instructions.add(instruction);
    }

    public void paintPath(Activity activity, Node nodeFrom, Node nodeTo) {
        ArrayList<LatLng> nodes = new ArrayList<>();
        for (int i = 0; i < shortestPath.size(); i++) {
            nodes.add(shortestPath.get(i).getLatLng());
        }
        mapHandler.addPolyline(ContextCompat.getColor(activity, R.color.black), MapHandler.POLYLINE_WIDTH_DEFAULT, nodes);
        zoomToPath();
    }


    public void zoomToPath() {
        LatLng[] latLngs = new LatLng[shortestPath.size()];
        for (int i = 0; i < shortestPath.size(); i++) {
            latLngs[i] = shortestPath.get(i).getLatLng();
        }
        mapHandler.zoomTo(true, latLngs);
    }

    private Node getCloserNode(MapHandler mapHandler, Node node) {
        float shortestDistance = Float.MAX_VALUE;
        float distance = 0;
        Node closerNode = null;
        if (!this.nodeList.isEmpty()) {
            for (Map.Entry<String, Node> entry : this.nodeList.entrySet()) {
                if (!entry.getValue().getType().equals("Camino")) {
                    distance = mapHandler.distance(node.getLatLng(), entry.getValue().getLatLng(), MapHandler.METERS);
                    if (distance < shortestDistance) {
                        shortestDistance = distance;
                        closerNode = entry.getValue();
                    }
                }
            }
        }
        return closerNode;
    }

    public LinkedList<Node> getShortestPath() {
        return shortestPath;
    }

    public LinkedList<Instruction> getInstructions() {
        return instructions;
    }
}
