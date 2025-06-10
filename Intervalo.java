
public class Intervalo {

    public int inicio;
    public int fim;

    public Intervalo(int inicio, int fim) {
        this.inicio = inicio;
        this.fim = fim;
    }

    public boolean conflitaCom(Intervalo outro) {
        return this.inicio < outro.fim && outro.inicio < this.fim;
    }
}
