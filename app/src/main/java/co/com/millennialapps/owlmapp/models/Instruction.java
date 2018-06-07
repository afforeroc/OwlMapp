package co.com.millennialapps.owlmapp.models;

/**
 * Created by erick on 6/11/2017.
 */

public class Instruction {

    private Node startNode;
    private Node finishNode;
    private int idResourceImage;
    private String description;

    public Instruction(Node node, int idResourceImage, String description) {
        this.startNode = node;
        this.finishNode = node;
        this.idResourceImage = idResourceImage;
        this.description = description;
    }

    public Instruction(Node startNode, Node finishNode, int idResourceImage, String description) {
        this.startNode = startNode;
        this.finishNode = finishNode;
        this.idResourceImage = idResourceImage;
        this.description = description;
    }

    public Node getStartNode() {
        return startNode;
    }

    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    public Node getFinishNode() {
        return finishNode;
    }

    public void setFinishNode(Node finishNode) {
        this.finishNode = finishNode;
    }

    public int getIdResourceImage() {
        return idResourceImage;
    }

    public void setIdResourceImage(int idResourceImage) {
        this.idResourceImage = idResourceImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
