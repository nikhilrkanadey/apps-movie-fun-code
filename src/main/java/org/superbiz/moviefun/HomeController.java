package org.superbiz.moviefun;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.superbiz.moviefun.albums.Album;
import org.superbiz.moviefun.albums.AlbumFixtures;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.superbiz.moviefun.movies.Movie;
import org.superbiz.moviefun.movies.MovieFixtures;
import org.superbiz.moviefun.movies.MoviesBean;

import java.util.Map;

@Controller
public class HomeController {

    private final MoviesBean moviesBean;
    private final AlbumsBean albumsBean;
    private final MovieFixtures movieFixtures;
    private final AlbumFixtures albumFixtures;
    private final PlatformTransactionManager platformTransactionManager;
    private final PlatformTransactionManager platformTransactionManagerAlbums;

    public HomeController(MoviesBean moviesBean,AlbumsBean  albumsBean, MovieFixtures movieFixtures, AlbumFixtures albumFixtures,
                          @Qualifier("moviesTM") PlatformTransactionManager transactionManager,@Qualifier("albumsTM")PlatformTransactionManager transactionManagerAlbums) {
        this.moviesBean = moviesBean;
        this.albumsBean = albumsBean;
        this.movieFixtures = movieFixtures;
        this.albumFixtures = albumFixtures;
        this.platformTransactionManager=transactionManager;
        this.platformTransactionManagerAlbums=transactionManagerAlbums;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/setup")

    public String setup(Map<String, Object> model) {

        TransactionStatus transactionStatus= platformTransactionManager.getTransaction(null);

        System.out.println("#####################################    Transaction status " + transactionStatus);

        moviesBean.addMovie(new Movie("Wedding Crashers", "David Dobkin", "Comedy", 7, 2005));
        moviesBean.addMovie(new Movie("Starsky & Hutch", "Todd Phillips", "Action", 6, 2004));
        moviesBean.addMovie(new Movie("Shanghai Knights", "David Dobkin", "Action", 6, 2003));
        moviesBean.addMovie(new Movie("I-Spy", "Betty Thomas", "Adventure", 5, 2002));
        moviesBean.addMovie(new Movie("The Royal Tenenbaums", "Wes Anderson", "Comedy", 8, 2001));
        moviesBean.addMovie(new Movie("Zoolander", "Ben Stiller", "Comedy", 6, 2001));
        moviesBean.addMovie(new Movie("Shanghai Noon", "Tom Dey", "Comedy", 7, 2000));

        platformTransactionManager.commit(transactionStatus);

        model.put("movies", moviesBean.getMovies());

       /* TransactionStatus transactionStatusAlb = platformTransactionManagerAlbums.getTransaction(null);
        albumsBean.addAlbum(new Album("artist","title",2017,10));

        System.out.println("#####################################    Transaction status " + transactionStatus.isCompleted());
        System.out.println("#####################################    Transaction status " + transactionStatus.isNewTransaction());
        platformTransactionManagerAlbums.commit(transactionStatusAlb);
        System.out.println("#####################################    Transaction status " + transactionStatus.isCompleted());
*/

        model.put("albums", albumsBean.getAlbums());

        return "setup";
    }

    public void createMovies(){
        TransactionStatus transactionStatus= platformTransactionManager.getTransaction(null);
        for (Movie movie : movieFixtures.load()) {
            moviesBean.addMovie(movie);
        }
        platformTransactionManager.commit(transactionStatus);
    }
    public void createAlbums(){
        TransactionStatus transactionStatus= platformTransactionManager.getTransaction(null);
        for (Album album : albumFixtures.load()) {
            albumsBean.addAlbum(album);
        }
        platformTransactionManager.commit(transactionStatus);
    }
}
