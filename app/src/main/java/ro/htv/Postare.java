package ro.htv;

public class Postare {
    public  String imRezEx;
    public String imRezProfil;
    public String NumePrenume;
    public String Descriere;

    public Postare(String imrez1, String imrez2, String Nume, String Des)
    {
        imRezEx = imrez1;
        imRezProfil = imrez2;
        NumePrenume = Nume;
        Descriere = Des;
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
