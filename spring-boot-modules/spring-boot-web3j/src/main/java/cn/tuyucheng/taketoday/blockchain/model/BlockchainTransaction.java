package cn.tuyucheng.taketoday.blockchain.model;

public class BlockchainTransaction {
   private String id;
   private int fromId;
   private int toId;
   private long value;
   private boolean accepted;

   public BlockchainTransaction() {
   }

   public BlockchainTransaction(int fromId, int toId, long value) {
      this.fromId = fromId;
      this.toId = toId;
      this.value = value;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public int getFromId() {
      return fromId;
   }

   public void setFromId(int fromId) {
      this.fromId = fromId;
   }

   public int getToId() {
      return toId;
   }

   public void setToId(int toId) {
      this.toId = toId;
   }

   public long getValue() {
      return value;
   }

   public void setValue(long value) {
      this.value = value;
   }

   public boolean isAccepted() {
      return accepted;
   }

   public void setAccepted(boolean accepted) {
      this.accepted = accepted;
   }
}