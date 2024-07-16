package com.movieflex.MovieApi.Dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class MovieDto {

    private  Integer movieId;

    @NotBlank(message = "Please Provide movie's title")
    private String title;

    @NotBlank(message = "Please Provide movie's director")
    private String director;

    @NotBlank(message = "Please Provide movie's studio")
    private String studio;


    private Set<String> movieCast;

    @NotNull(message = "Release year cannot be null")
    private Integer releaseYear;

    @NotBlank(message = "Please Provide movie's poster")
    private String poster;

    @NotBlank(message = "Please Provide movie's poster")
    private String posterUrl;
}
