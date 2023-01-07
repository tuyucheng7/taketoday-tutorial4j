package cn.tuyucheng.taketoday.maps;

public class CoordinateSlowKey extends CoordinateKey {

    public CoordinateSlowKey(int x, int y) {
        super(x, y);
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
