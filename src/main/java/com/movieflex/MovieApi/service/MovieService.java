package com.movieflex.MovieApi.service;

import com.movieflex.MovieApi.Dto.MovieDto;
import com.movieflex.MovieApi.Dto.MoviePageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MovieService {

    MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException;

    MovieDto getMovie(Integer movieId);
    List<MovieDto>  getAllMovies();

    MovieDto updatemovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException;

    String deleteMovie(Integer movieId) throws IOException;

    MoviePageResponse getAllMoviesWithPagination( Integer pageNumber, Integer pageSize);
     MoviePageResponse getAllMoviesWithPaginationAndSortion( Integer pageNumber, Integer pageSize, String sortBy, String dir);

}
