package ro.htv;

public class Postare {
    public  String imRezEx;
    public String imRezProfil;
    public String NumePrenume;
    public String Descriere;
    public int tip;

    public int getTip() {
        return tip;
    }

    public Postare(String imrez1, String imrez2, String Nume, String Des, int tipp)
    {
        imRezEx = imrez1;
        imRezProfil = imrez2;
        NumePrenume = Nume;
        Descriere = Des;
        tip = tipp;
    }

    public String getImRezEx() {
        return imRezEx;
    }

    public String getImRezProfil() {
        return imRezProfil;
    }

    public String getNumePrenume() {
        return NumePrenume;
    }

    public String getDescriere() {
        return Descriere;
    }
}
