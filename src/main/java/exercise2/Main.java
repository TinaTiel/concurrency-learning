package exercise2;

import java.math.BigInteger;

public class Main {

    public static void main(String[] args) {

    }

    static class ComplexCalculation {

        public BigInteger calculateResult(BigInteger base1, BigInteger power1, BigInteger base2, BigInteger power2) {
            BigInteger result = BigInteger.ZERO;

            PowerCalculatingThread thread1 = new PowerCalculatingThread(base1, power1);
            PowerCalculatingThread thread2 = new PowerCalculatingThread(base2, power2);
            thread1.start();
            thread2.start();
            try {
                thread1.join();
                thread2.join();
                result = thread1.getResult().add(thread2.getResult());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return result;
        }

        private static class PowerCalculatingThread extends Thread {
            private BigInteger result = BigInteger.ONE;
            private BigInteger base;
            private BigInteger power;

            public PowerCalculatingThread(BigInteger base, BigInteger power) {
                this.base = base;
                this.power = power;
            }

            @Override
            public void run() {
                BigInteger result = BigInteger.ONE;
                for(BigInteger i = BigInteger.ZERO; i.compareTo(power) < 0; i = i.add(BigInteger.ONE)) {
                    result = result.multiply(i);
                }
                this.result = result;
            }

            public BigInteger getResult() { return result; }
        }
    }
}
