module.exports = function(grunt) {
    // Do grunt-related things in here

    grunt.loadNpmTasks('grunt-contrib-jasmine');

    grunt.initConfig({
        jasmine: {
          identitynowclient: {
            src: 'src/**/*.js',
            options: {
              specs: 'spec/*Spec.js',
              helpers: 'spec/*Helper.js',
              stopSpecOnExpectationFailure: true
            }
          }
        }
    });
};