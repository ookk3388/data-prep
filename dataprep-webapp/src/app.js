(function() {
    'use strict';

    angular.module('data-prep',
        [
            'data-prep-utils', //utils components: constants, filters, ...
            'data-prep-dataset', //dataset getter, manipulation, etc
            
            'talend.widget', //compoonents widget built on bourbon (modal, dropdown, ...)

            'ui.router'
        ])

        .config(function ($stateProvider, $urlRouterProvider) {
            $stateProvider
                .state('nav', {
                    abstract: true,
                    templateUrl: 'app/navbar/navbar.html'
                })
                .state('nav.home', {
                    url: '/home',
                    templateUrl: 'app/home/home.html',
                    controller: 'HomeCtrl',
                    controllerAs: 'homeCtrl'
                })
                .state('nav.dataset', {
                    url: '/dataset/:datasetId',
                    templateUrl: 'app/dataset/dataset.html',
                    controller: 'DatasetCtrl',
                    controllerAs: 'datasetCtrl',
                    resolve:{
                        datasetDetails: ['$stateParams', 'DatasetService', function($stateParams, DatasetService) {
                            return DatasetService.getDataFromId($stateParams.datasetId, true);
                        }]
                    }
                });

            $urlRouterProvider.otherwise('/home');
        });
})();
