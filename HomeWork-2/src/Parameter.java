public class Parameter {

    private double aValue, alphaSquaredValue, betaSquaredValue;

    public Parameter(double aValue, double alphaSquaredValue, double betaSquaredValue) {
        this.aValue = aValue;
        this.alphaSquaredValue = alphaSquaredValue;
        this.betaSquaredValue = betaSquaredValue;
    }

    public double getAValue() {
        return aValue;
    }

    public double getAlphaSquaredValue() {
        return alphaSquaredValue;
    }

    public double getBetaSquaredValue() {
        return betaSquaredValue;
    }
}
