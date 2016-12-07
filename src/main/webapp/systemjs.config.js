/**
 * System configuration for Angular 2 samples
 * Adjust as necessary for your application needs.
 */
(function (global) {
    var pathToNpm = {
        'npm:': 'node_modules/'
    };
    var map = {
        exchangeservice: 'exchangeservice',
        // angular libraries
        '@angular': 'npm:@angular',
        'angular2-in-memory-web-api': 'npm:angular2-in-memory-web-api',
        // other libraries
        'ng2-bootstrap/ng2-bootstrap': 'npm:ng2-bootstrap/bundles/ng2-bootstrap.umd.js',
        'rxjs': 'npm:rxjs',
        'moment': 'npm:moment/moment.js'
    };
    var packages = {
        exchangeservice: {
            main: './js-out/exchangeservice/main',
            defaultExtension: 'js'
        },
        rxjs: {
            defaultExtension: 'js'
        },
        'angular2-in-memory-web-api': {
            main: 'index',
            defaultExtension: 'js'
        }
    };
    var ngPackageNames = [
      'common',
      'compiler',
      'core',
      'forms',
      'http',
      'platform-browser',
      'platform-browser-dynamic',
      'router',
      'upgrade'
    ];

    // packages for Karma environments
    function packIndex(packageName) {
        packages['@angular/' + packageName] = { main: 'index', defaultExtension: 'js' };
    }

    // packages for UMD environments
    function packUmd(packageName) {
        packages['@angular/' + packageName] = { main: '/bundles/' + packageName + '.umd', defaultExtension: 'js' };
    }

    var setPackageConfiguration = System.packageWithIndex ? packIndex : packUmd;
    // add package entries for angular packages
    ngPackageNames.forEach(setPackageConfiguration);

    System.config({
        paths: pathToNpm,
        // map tells the System loader where to look for things
        map: map,
        // packages tells the System loader how to load when no filename and/or no extension
        packages: packages
    });
})(this);
