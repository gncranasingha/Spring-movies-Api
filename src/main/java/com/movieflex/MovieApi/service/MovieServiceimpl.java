package com.movieflex.MovieApi.service;

import com.movieflex.MovieApi.Dto.MovieDto;
import com.movieflex.MovieApi.Dto.MoviePageResponse;
import com.movieflex.MovieApi.entities.Movie;
import com.movieflex.MovieApi.exceptions.FileExistsException;
import com.movieflex.MovieApi.exceptions.MovieNotFoundException;
import com.movieflex.MovieApi.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


@Service
public class MovieServiceimpl implements MovieService{

    private final MovieRepository movieRepository;
    private  final FileService fileService;

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    public MovieServiceimpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {

        //1. upload the file
        boolean exists = Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()));
        if(exists){
            throw new FileExistsException("File already exists ! Please enter another file name !");
        }
        String uploadFileName = fileService.uploadFile(path, file);

        //2. set the value of field 'poster' as fileName
        movieDto.setPoster(uploadFileName);

        //3. map dto to movie object
        Movie movie = new Movie(
                null,
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );

        //4. save the movie object -> saved movie object
        Movie saveMovie = movieRepository.save(movie);


        //5. generate the posterUrl
        String posterUrl = baseUrl + "/file/" + uploadFileName;

        //6. map movie object to dto object and return it
        MovieDto response = new MovieDto(
                saveMovie.getMovieId(),
                saveMovie.getTitle(),
                saveMovie.getDirector(),
                saveMovie.getStudio(),
                saveMovie.getMovieCast(),
                saveMovie.getReleaseYear(),
                saveMovie.getPoster(),
                posterUrl
        );
         return response;
    }

    @Override
    public MovieDto getMovie(Integer movieId) {

        //1.check the data in DB and of exists, fetch the data of given ID
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie not found with Id = " + movieId));

        //5. generate the posterUrl
        String posterUrl = baseUrl + "/file/" + movie.getPoster();

        //3. map to moveDto object and return it
        MovieDto response = new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );

        return response;
    }

    @Override
    public List<MovieDto> getAllMovies() {

        //1. fetch all data from DB
        List<Movie> movies = movieRepository.findAll();

        List<MovieDto> movieDtos = new ArrayList<>();

        //2. iterate through the list, generate posterUrl for each movie obj, and map MovieDto obj
        for(Movie movie : movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(movieDto);
        }

        return movieDtos;
    }

    @Override
    public MovieDto updatemovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException {

        //1. check if movie object exists with given movieId
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie not found with Id = " + movieId));

        //2. if file is null, do nothing
        //if file is not null, then delete existing file associated with the record,
        // and upload the new file
        String fileName = movie.getPoster();
        if(file != null ) {
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
        }

        //3. set movieDto's poster value, according to step2
        movieDto.setPoster(fileName);

        //4. map it to Movie object
        Movie movie1 = new Movie(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster()
        );

        //5.save the movie object -> return saved movie object
        Movie updatedMovie = movieRepository.save(movie1);

        //6. generate posterurl for it
        String posterUrl = baseUrl + "/file/" + fileName;

        //7.map to movieDto and return it
        MovieDto response = new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );

        return response;
    }

    @Override
    public String deleteMovie(Integer movieId) throws IOException {

        // 1. check if movie object exists in DB
        Movie mv = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie not found with Id = " + movieId));
        Integer id = mv.getMovieId();

        //2. delete the file associated with this object
        Files.deleteIfExists(Paths.get(path + File.separator + mv.getPoster()));

        //3. delete the movie object
        movieRepository.delete(mv);

        return "Movie Deleted with id = " + id;
    }


    //Pagination And Sorting
    @Override
    public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Movie> moviePages = movieRepository.findAll(pageable);
        List<Movie> movies = moviePages.getContent();

        List<MovieDto> movieDtos = new ArrayList<>();

        //2. iterate through the list, generate posterUrl for each movie obj, and map MovieDto obj
        for (Movie movie : movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(movieDto);
        }

            return new MoviePageResponse(movieDtos, pageNumber, pageSize, moviePages.getTotalElements(), moviePages.getTotalPages(), moviePages.isLast());
        }
        //sorting part
        @Override
        public MoviePageResponse getAllMoviesWithPaginationAndSortion (Integer pageNumber, Integer pageSize, String
        sortBy, String dir){

            Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(dir).descending();

            Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

            Page<Movie> moviePages = movieRepository.findAll(pageable);
            List<Movie> movies = moviePages.getContent();

            List<MovieDto> movieDtos = new ArrayList<>();

            //2. iterate through the list, generate posterUrl for each movie obj, and map MovieDto obj
            for (Movie movie : movies) {
                String posterUrl = baseUrl + "/file/" + movie.getPoster();
                MovieDto movieDto = new MovieDto(
                        movie.getMovieId(),
                        movie.getTitle(),
                        movie.getDirector(),
                        movie.getStudio(),
                        movie.getMovieCast(),
                        movie.getReleaseYear(),
                        movie.getPoster(),
                        posterUrl
                );
                movieDtos.add(movieDto);
            }

            return new MoviePageResponse(movieDtos, pageNumber, pageSize, moviePages.getTotalElements(), moviePages.getTotalPages(), moviePages.isLast());

        }
    }
