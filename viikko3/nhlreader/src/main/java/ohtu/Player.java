
package ohtu;

public class Player implements Comparable<Player> {
    private String name;
    private String nationality;
    private String team;
    private int goals;
    private int assists;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    } 

    public String getNationality() {
        return nationality;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getTeam() {
        return team;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public int getGoals() {
        return goals;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public int getAssists() {
        return assists;
    }

    public int points() {
        return goals + assists;
    }

    @Override
    public int compareTo(Player player) {
        return player.points() - this.points();
    }

    @Override
    public String toString() {
        return name + " " + team + " " + goals + " + " + assists + " = " + points();
    }

    public void tulostaPelaajanTiedot() {
        System.out.format("%-18s%10s%6d%3s%3d%3s%3d%n", name, team, goals, " + ", assists, " = ", points());
    }
      
}
