/**
 * Created by alexands on 05.12.2016.
 */
var gulp = require('gulp'),
    webserver = require('gulp-webserver'),
    typescript = require('gulp-typescript'),
    sourcemaps = require('gulp-sourcemaps'),
    tscConfig = require('./tsconfig.json');

var appSrc = 'app/js-out/',
    tsSrc = 'app/',
    htmlSrc = 'html/',
    cssSrc = 'css/';

gulp.task('html', function() {
    gulp.src(htmlSrc + '**/*.html');
});

gulp.task('css', function() {
    gulp.src(cssSrc + '**/*.css');
});

gulp.task('typescript', function () {
    return gulp
        .src(tsSrc + '**/*.ts')
        .pipe(sourcemaps.init())
        .pipe(typescript(tscConfig.compilerOptions))
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest(appSrc + 'js/'));
});

gulp.task('watch', function() {
    gulp.watch(tsSrc + '**/*.ts', ['typescript']);
    gulp.watch(cssSrc + '**/*.css', ['css']);
    gulp.watch(htmlSrc + '**/*.html', ['html']);
});

gulp.task('webserver', function() {
    gulp.src(appSrc)
        .pipe(webserver({
            livereload: true,
            port: 8080,
            open: true
        }));
});

gulp.task('default', ['typescript', 'watch', 'webserver']);