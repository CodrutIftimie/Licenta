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
    Altceva(20);

    public int id;

    Services(int id) {
        this.id = id;
    }

    public static Services getById(int id) {
        for(Services e : values()) {
            if(e.id == id)
                return e;
        }
        return Altceva;
    }

    public static boolean isService(String name) {
        for(Services e: values()) {
            if(e.toString().equals(name))
                return true;
        }
        return false;
    }
}
