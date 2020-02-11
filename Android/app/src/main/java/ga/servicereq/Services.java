package ga.servicereq;

public enum Services {
    Cunoștințe(0),
    IT(1),
    Design(2),
    Arte_și_Amuzament(3),
    Creativitate(4),
    Politică(5),
    Educație_și_creșterea_copilului(6),
    Construcții(7),
    Curățenie(8),
    Financiar(9),
    Agenți_și_Vânzări(10),
    Sănătate(11),
    Sport_și_Fitness(12),
    Ospitalitate(13),
    Transport(14),
    Utilități(15),
    Asigurări(16),
    Mâncare_și_Băuturi(17),
    Închirieri(18),
    Evenimente(19),
    Altceva(20),


    Knowledge(30),
    Arts_and_Entertainment(33),
    Creative(34),
    Government(35),
    Education_and_Childcare(36),
    Construction(37),
    Cleaning(38),
    Financial(39),
    Agents_and_Brokers(40),
    Health_Care(41),
    Sports_and_Fitness(42),
    Hospitality(43),
    Utilities(45),
    Insurance(46),
    Food_and_Beverages(47),
    Rentals(48),
    Events(49),
    Other(50),

    UNKNOWN(100);

    public int id;

    Services(int id) {
        this.id = id;
    }

    public static Services getById(int id) {
        for (Services e : values()) {
            if (e.id == id)
                return e;
        }
        return UNKNOWN;
    }

    public static boolean isService(String name) {
        for (Services e : values()) {
            if (e.toString().equals(name) || e.toString().equals(name.replace(" ", "_")))
                return true;
        }
        return false;
    }

    public String toEnglishString() {
        if (this.id != 1 && this.id != 2 && this.id != 14) {
            return getById(this.id+30).toString();
        }
        return getById(this.id).toString();
    }

    public String fromEnglishString() {
        if (this.id != 1 && this.id != 2 && this.id != 14) {
            return getById(this.id-30).toString();
        }
        return getById(this.id).toString();
    }

    public static String[] getArray() {
        String[] categories = new String[21];
        for(int i=0;i<21;i++)
            categories[i] = getById(i).toString().replace("_", " ");
        return categories;
    }
}
