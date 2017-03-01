(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.transformation-params.directive:TransformParams
     * @description This directive display a transformation parameters form
     * @restrict E
     * @usage
     <transform-params
             transformation="transformation"
             on-submit="callback()">
     </transform-params>
     * @param {object} transformation The transformation containing parameters
     * @param {function} onSubmit The callback executed on form submit
     * @param {function} onSubmitHoverOn The callback executed on mouseenter on form submit
     * @param {function} onSubmitHoverOff The callback executed on mouseleave on form submit
     */
    function TransformParams() {
        return {
            restrict: 'E',
            templateUrl: 'components/transformation/params/transformation-params.html',
            replace: true,
            scope: {
                transformation: '=',
                onSubmit: '&',
                onSubmitHoverOn: '&',
                onSubmitHoverOff: '&'
            },
            bindToController: true,
            controllerAs: 'paramsCtrl',
            controller: 'TransformParamsCtrl'
        };
    }

    angular.module('data-prep.transformation-params')
        .directive('transformParams', TransformParams);
})();