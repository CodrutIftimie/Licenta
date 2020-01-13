package ga.servicereq;

public enum Services {
    Knowledge(0),
    IT(1),
    Design(2),
    Arts_and_Entertainment(3),
    Creative(4),
    Government(5),
    Education_and_Childcare(6),
    Construction(7),
    Cleaning(8),
    Financial(9),
    Agents_and_Brokers(10),
    Health_Care(11),
    Wellness_and_Personal_Grooming(12),
    Sports_and_Fitness(13),
    Hospitality(14),
    Transport(15),
    Utilities(16),
    Insurance(17),
    Food_and_Beverages(18),
    Rentals(19),
    Events(20),
    UNKNOWN(30);

    public int id;

    Services(int id) {
        this.id = id;
    }

    public static Services getById(int id) {
        for(Services e : values()) {
            if(e.id == id)
                return e;
        }
        return UNKNOWN;
    }

    public static boolean isService(String name) {
        for(Services e: values()) {
            if(e.toString().equals(name))
                return true;
        }
        return false;
    }
}
