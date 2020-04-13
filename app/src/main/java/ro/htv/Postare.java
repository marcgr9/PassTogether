package ro.htv;

public class Postare {
    public int imRezEx;
    public int imRezProfil;
    public String NumePrenume;
    public String Descriere;

    public Postare(int imrez1, int imrez2, String Nume, String Des)
    {
        imRezEx = imrez1;
        imRezProfil = imrez2;
        NumePrenume = Nume;
        Descriere = Des;
    }

    public int getImRezEx() {
        return imRezEx;
    }

    public int getImRezProfil() {
        return imRezProfil;
    }

    public String getNumePrenume() {
        return NumePrenume;
    }

    public String getDescriere() {
        return Descriere;
    }
}
