public class Asset {
    public static enum AssetType {
        HARDWARE, SOFTWARE, ELECTRONICS
    }
    private final int assetId;
    private String assetName;
    private AssetType assetType;
    private int assetCount;

    //Constructor
    Asset(int assetId, String assetName, AssetType assetType, int assetCount) {
        this.assetId = assetId;
        this.assetName = assetName;
        this.assetType = assetType;
        this.assetCount = assetCount;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public void setAssetCount(int assetCount) {
        this.assetCount = assetCount;
    }

    public int getAssetId(){
        return assetId;
    }

    public String getAssetName(){
        return assetName;
    }

    public AssetType getAssetType(){
        return assetType;
    }

    public int getAssetCount(){
        return assetCount;
    }

    public String toString() {
        String s = "";
        s = s + "AssetId : " + assetId + "\t Asset Name : " + assetName + "\t Asset Type : " + assetType + "\t Asset Count : " + assetCount + "\n";
        return s;
    }
}
