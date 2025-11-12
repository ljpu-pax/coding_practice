package sig;

// #movieId,title,date,genres,
// 1,Toy Story,1995,Adventure|Animation|Children|Comedy|Fantasy,
// 2.Jumanji,1995,Adventure|Children|Fantasy,

// 码农岗，我看网上题也不多。
// 给你一个包含movieId,title,genres的csv文件和一个写好的MovieCatalog class，
// 要求refactor 里面的constructor和List<Movie> filter(String genre, int startYear, int endYear) method，
// 给你的实现就是一个一个找，要求优化filter method，对constructor的时间空间没有要求
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class Movie {
    int movieId;
    String title;
    int year;
    List<String> genres;

    public Movie(int movieId, String title, int year, List<String> genres) {
        this.movieId = movieId;
        this.title = title;
        this.year = year;
        this.genres = genres;
    }
}

public class MovieCatalog {
    // genre -> year -> list of movies
    Map<String, TreeMap<Integer, List<Movie>>> genreYearMap = new HashMap<>();

    public MovieCatalog(String csvFilePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(csvFilePath));
        
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#")) continue;
            String[] parts = line.split(",", 4);
            int movieId = Integer.parseInt(parts[0].trim());
            String title = parts[1].trim();
            int year = Integer.parseInt(parts[2].trim());
            List<String> genres = Arrays.asList(parts[3].trim().split("\\|"));
            
            Movie movie = new Movie(movieId, title, year, genres);

            for (String genre : genres) {
                genreYearMap
                    .computeIfAbsent(genre, k -> new TreeMap<>())
                    .computeIfAbsent(year, k -> new ArrayList<>()).add(movie);
            }
        }

        reader.close();

    }

    public List<Movie> filter(String genre, int startYear, int endYear) {
        List<Movie> result = new ArrayList<>();

        if (!genreYearMap.containsKey(genre)) return result;

         // subMap is efficient due to TreeMap
         NavigableMap<Integer, List<Movie>> yearMap = genreYearMap.get(genre)
            .subMap(startYear, true, endYear, true);

        for (List<Movie> moviesInYear : yearMap.values()) {
            result.addAll(moviesInYear);
        }

        return result;
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // Write a sample CSV to a temporary file
        String csvContent =
            "#movieId,title,year,genres,\n" +
            "1,Toy Story,1995,Adventure|Animation|Children|Comedy|Fantasy,\n" +
            "2,Jumanji,1995,Adventure|Children|Fantasy,\n" +
            "3,Heat,1995,Action|Crime|Drama|Thriller,\n" +
            "4,Sense and Sensibility,1995,Drama|Romance,\n" +
            "5,American President,1995,Comedy|Drama|Romance,\n";

        File tempFile = File.createTempFile("movies", ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write(csvContent);
        }

        MovieCatalog catalog = new MovieCatalog(tempFile.getAbsolutePath());

        System.out.println("Test 1: Comedy movies between 1990 and 1995");
        List<Movie> result1 = catalog.filter("Comedy", 1990, 1995);
        for (Movie m : result1) {
            System.out.println(m.title + " (" + m.year + ")");
        }
        System.out.println("Expected: 2 results");

        System.out.println("\nTest 2: Action movies between 1994 and 1995");
        List<Movie> result2 = catalog.filter("Action", 1994, 1995);
        for (Movie m : result2) {
            System.out.println(m.title + " (" + m.year + ")");
        }
        System.out.println("Expected: 1 result");

        System.out.println("\nTest 3: Horror movies between 1990 and 2000");
        List<Movie> result3 = catalog.filter("Horror", 1990, 2000);
        System.out.println("Number of results: " + result3.size());
        System.out.println("Expected: 0 results");
    }
}

