package sig;

import java.io.*;
import java.util.*;

public class MovieCatalogTest {

	// Movie class
	static class Movie {
		final int releaseYear;
		final int id;
		final String title;
		final List<String> genres;

		Movie(int id, String title, int year, List<String> genres) {
			this.id = id;
			this.title = title;
			this.releaseYear = year;
			this.genres = genres;
		}

		@Override
		public String toString() {
			return String.format("Id: %d, Title: %s, Release Year: %d, Genres: %s",
					this.id, this.title, this.releaseYear, String.join(",", this.genres));
		}
	}

	// MovieCatalog class with optimized structure
	static class MovieCatalog {
		private final Map<String, TreeMap<Integer, List<Movie>>> genreYearMap = new HashMap<>();

		MovieCatalog(String movieFileName) throws IOException {
			try (BufferedReader br = new BufferedReader(new FileReader(movieFileName))) {
				String line;
				while ((line = br.readLine()) != null) {
					if (line.startsWith("#")) continue;

					String[] lineSplit = line.split(",", -1);
					if (lineSplit.length < 4) continue;

					int id = Integer.parseInt(lineSplit[0]);
					String title = lineSplit[1];
					int year = Integer.parseInt(lineSplit[2]);
					List<String> genres = Arrays.asList(lineSplit[3].split("\\|"));

					Movie movie = new Movie(id, title, year, genres);

					for (String genre : genres) {
						genreYearMap
								.computeIfAbsent(genre, g -> new TreeMap<>())
								.computeIfAbsent(year, y -> new ArrayList<>())
								.add(movie);
					}
				}
			}
		}

		ArrayList<Movie> GetMovies(String genre, int startYear, int endYear) {
			ArrayList<Movie> result = new ArrayList<>();

			TreeMap<Integer, List<Movie>> yearMap = genreYearMap.get(genre);
			if (yearMap == null || startYear > endYear) return result;

			for (List<Movie> movies : yearMap.subMap(startYear, true, endYear, true).values()) {
				result.addAll(movies);
			}

			return result;
		}
	}

	// Main method with tests
	public static void main(String[] args) {
		try {
			// Create a temporary test file
			File tempFile = File.createTempFile("movies", ".csv");
			try (PrintWriter writer = new PrintWriter(tempFile)) {
				writer.println("#movieId,title,date,genres,");
				writer.println("1,Toy Story,1995,Adventure|Animation|Children|Comedy|Fantasy,");
				writer.println("2,Jumanji,1995,Adventure|Children|Fantasy,");
				writer.println("3,Grumpier Old Men,1995,Comedy|Romance,");
				writer.println("84764,Werewolves on Wheels,1971,Horror,");
				writer.println("84766,Fantasma,2006,Drama,");
				writer.println("131262,Innocence,2014,Adventure|Fantasy|Horror,");
			}

			MovieCatalog catalog = new MovieCatalog(tempFile.getAbsolutePath());

			// Test cases
			System.out.println("=== Comedy movies from 1990 to 2000 ===");
			for (Movie m : catalog.GetMovies("Comedy", 1990, 2000)) {
				System.out.println(m);
			}

			System.out.println("\n=== Horror movies from 1970 to 1980 ===");
			for (Movie m : catalog.GetMovies("Horror", 1970, 1980)) {
				System.out.println(m);
			}

			System.out.println("\n=== Adventure movies from 1994 to 1995 ===");
			for (Movie m : catalog.GetMovies("Adventure", 1994, 1995)) {
				System.out.println(m);
			}

			System.out.println("\n=== Drama movies from 2000 to 2010 ===");
			for (Movie m : catalog.GetMovies("Drama", 2000, 2010)) {
				System.out.println(m);
			}

			tempFile.deleteOnExit();
		} catch (IOException e) {
			System.err.println("Failed to run test: " + e.getMessage());
		}
	}
}
