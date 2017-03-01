describe('Playground Service', function () {
    'use strict';

    var content = {column: [], records: []};

    beforeEach(module('data-prep.services.playground'));

    beforeEach(inject(function ($injector, $q, DatasetService, FilterService, RecipeService, DatagridService, PreparationService) {
        spyOn(DatasetService, 'getContent').and.returnValue($q.when(content));
        spyOn(FilterService, 'removeAllFilters').and.callFake(function() {});
        spyOn(RecipeService, 'reset').and.callFake(function() {});
        spyOn(DatagridService, 'setDataset').and.callFake(function() {});
        spyOn(PreparationService, 'create').and.returnValue($q.when(true));
        spyOn(PreparationService, 'setName').and.returnValue($q.when(true));
    }));

    it('should init visible flag to false', inject(function(PlaygroundService) {
        //then
        expect(PlaygroundService.visible).toBe(false);
    }));

    it('should set visible flag to true', inject(function(PlaygroundService) {
        //given
        PlaygroundService.visible = false;

        //when
        PlaygroundService.show();

        //then
        expect(PlaygroundService.visible).toBe(true);
    }));

    it('should set visible flag to true', inject(function(PlaygroundService) {
        //given
        PlaygroundService.visible = true;

        //when
        PlaygroundService.hide();

        //then
        expect(PlaygroundService.visible).toBe(false);
    }));

    describe('init new preparation', function() {
        var dataset = {id: 'e85afAa78556d5425bc2'};
        var assertNewPreparationInitialization;

        beforeEach(inject(function(PlaygroundService, DatasetService, FilterService, RecipeService, DatagridService) {
            assertNewPreparationInitialization = function() {
                expect(PlaygroundService.currentMetadata).toEqual(dataset);
                expect(PlaygroundService.currentData).toEqual(content);
                expect(FilterService.removeAllFilters).toHaveBeenCalled();
                expect(RecipeService.reset).toHaveBeenCalled();
                expect(DatagridService.setDataset).toHaveBeenCalledWith(dataset, content);
            };
        }));

        it('should init a new preparation and show playground when there is no loaded data yet', inject(function($rootScope, PlaygroundService, PreparationService) {
            //given
            expect(PlaygroundService.visible).toBe(false);
            expect(PlaygroundService.currentMetadata).toBeFalsy();
            expect(PlaygroundService.currentData).toBeFalsy();
            expect(PreparationService.currentPreparationId).toBeFalsy();
            expect(PreparationService.preparationName).toBeFalsy();
            expect(PreparationService.originalPreparationName).toBeFalsy();

            //when
            PlaygroundService.initPlayground(dataset);
            $rootScope.$digest();

            //then
            assertNewPreparationInitialization();
        }));

        it('should init a new preparation and show playground when there is already a created preparation yet', inject(function($rootScope, PlaygroundService, PreparationService) {
            //given
            PlaygroundService.currentMetadata = {id : 'e85afAa78556d5425bc2'};
            PreparationService.currentPreparationId = '12342305304543';

            expect(PlaygroundService.visible).toBe(false);
            expect(PlaygroundService.currentMetadata).toBeTruthy();
            expect(PlaygroundService.currentData).toBeFalsy();
            expect(PreparationService.currentPreparationId).toBeTruthy();

            //when
            PlaygroundService.initPlayground(dataset);
            $rootScope.$digest();

            //then
            assertNewPreparationInitialization();
        }));

        it('should init a new preparation and show playground when the loaded dataset is not the wanted dataset', inject(function($rootScope, PlaygroundService, PreparationService) {
            //given
            PlaygroundService.currentMetadata = {id : 'ab45420c09bf98d9a90'};

            expect(PlaygroundService.visible).toBe(false);
            expect(PlaygroundService.currentMetadata).toBeTruthy();
            expect(PlaygroundService.currentData).toBeFalsy();
            expect(PreparationService.currentPreparationId).toBeFalsy();

            //when
            PlaygroundService.initPlayground(dataset);
            $rootScope.$digest();

            //then
            assertNewPreparationInitialization();
        }));

        it('should reset preparation name', inject(function($rootScope, PlaygroundService) {
            //given
            PlaygroundService.preparationName = 'preparation name';
            PlaygroundService.originalPreparationName = 'preparation name';

            //when
            PlaygroundService.initPlayground(dataset);
            $rootScope.$digest();

            //then
            expect(PlaygroundService.preparationName).toBeFalsy();
            expect(PlaygroundService.originalPreparationName).toBeFalsy();
        }));

        it('should init playground when the wanted dataset is loaded and no preparation was created yet', inject(function($rootScope, PlaygroundService, FilterService, RecipeService, DatagridService) {
            //given
            var dataset = {id: 'e85afAa78556d5425bc2'};
            var data = [{column: [], records: []}];
            PlaygroundService.currentMetadata = dataset;
            PlaygroundService.currentData = data;

            expect(PlaygroundService.visible).toBe(false);
            expect(PlaygroundService.currentPreparationId).toBeFalsy();

            //when
            PlaygroundService.initPlayground(dataset);
            $rootScope.$digest();

            //then
            expect(PlaygroundService.currentMetadata).toBe(dataset);
            expect(PlaygroundService.currentData).toBe(data);
            expect(FilterService.removeAllFilters).not.toHaveBeenCalled();
            expect(RecipeService.reset).not.toHaveBeenCalled();
            expect(DatagridService.setDataset).not.toHaveBeenCalled();
        }));
    });

    it('should set new name to a new to the preparation', inject(function($rootScope, PlaygroundService, PreparationService) {
        //given
        var name = 'My preparation';
        var newName = 'My new preparation name';
        PreparationService.currentPreparationId = 'e85afAa78556d5425bc2';

        PlaygroundService.preparationName = name;
        PlaygroundService.originalPreparationName = name;
        PlaygroundService.currentMetadata = {id: '123d120394ab0c53'};

        //when
        PlaygroundService.preparationName = newName;
        PlaygroundService.createOrUpdatePreparation(newName);
        $rootScope.$digest();

        //then
        expect(PreparationService.create).not.toHaveBeenCalled();
        expect(PreparationService.setName).toHaveBeenCalledWith({id: '123d120394ab0c53'}, newName);
        expect(PlaygroundService.preparationName).toBe(newName);
        expect(PlaygroundService.originalPreparationName).toBe(newName);
    }));

    describe('load existing dataset', function() {
        var data = {
            records: [{id: '0', firstname: 'toto'}, {id: '1', firstname: 'tata'}, {id: '2', firstname: 'titi'}]
        };

        beforeEach(inject(function($rootScope, $q, PreparationService, RecipeService) {
            spyOn($rootScope, '$emit').and.callThrough();
            spyOn(PreparationService, 'getContent').and.returnValue($q.when({data: data}));
            spyOn(RecipeService, 'refresh').and.callFake(function() {});
            spyOn(RecipeService, 'disableStepsAfter').and.callFake(function() {});
        }));

        it('should load existing preparation when it is not already loaded', inject(function($rootScope, PlaygroundService, PreparationService, FilterService, RecipeService, DatagridService) {
            //given
            var preparation = {
                id: '6845521254541',
                datasetId: '1'
            };
            PreparationService.currentPreparationId = '5746518486846';

            //when
            PlaygroundService.load(preparation);
            expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.start');
            $rootScope.$apply();

            //then
            expect(PlaygroundService.currentMetadata).toEqual({id: '1'});
            expect(PlaygroundService.currentData).toBe(data);
            expect(FilterService.removeAllFilters).toHaveBeenCalled();
            expect(RecipeService.refresh).toHaveBeenCalled();
            expect(DatagridService.setDataset).toHaveBeenCalledWith(preparation.dataset, data);
            expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.stop');
        }));

        it('should not change playground if the preparation to load is already loaded', inject(function($rootScope, PlaygroundService, PreparationService, FilterService, RecipeService, DatagridService) {
            //given
            var preparation = {
                id: '6845521254541',
                dataset: {id: '1', name: 'my dataset'}
            };
            var data = {};
            var metadata = {};

            PreparationService.currentPreparationId = '6845521254541';
            PlaygroundService.currentMetadata = metadata;
            PlaygroundService.currentData = data;

            //when
            PlaygroundService.load(preparation);
            $rootScope.$apply();

            //then
            expect(PlaygroundService.currentMetadata).toBe(metadata);
            expect(PlaygroundService.currentData).toBe(data);
            expect(FilterService.removeAllFilters).not.toHaveBeenCalled();
            expect(RecipeService.refresh).not.toHaveBeenCalled();
            expect(DatagridService.setDataset).not.toHaveBeenCalled();
            expect($rootScope.$emit).not.toHaveBeenCalled();
        }));

        it('should load preparation content at a specific step', inject(function($rootScope, PlaygroundService, FilterService, RecipeService, DatagridService) {
            //given
            var step = {
                transformation: {stepId: 'a4353089cb0e039ac2'}
            };
            var metadata = {id: '1', name: 'my dataset'};
            PlaygroundService.currentMetadata = metadata;

            //when
            PlaygroundService.loadStep(step);
            expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.start');
            $rootScope.$apply();

            //then
            expect(PlaygroundService.currentMetadata).toBe(metadata);
            expect(PlaygroundService.currentData).toBe(data);
            expect(FilterService.removeAllFilters).not.toHaveBeenCalled();
            expect(RecipeService.refresh).not.toHaveBeenCalled();
            expect(RecipeService.disableStepsAfter).toHaveBeenCalledWith(step);
            expect(DatagridService.setDataset).toHaveBeenCalledWith(metadata, data);
            expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.stop');
        }));

        it('should do nothing if current step (threshold between active and inactive) is already selected', inject(function($rootScope, PlaygroundService, RecipeService, PreparationService) {
            //given
            var step = {
                transformation: {stepId: 'a4353089cb0e039ac2'}
            };
            spyOn(RecipeService, 'getActiveThresholdStep').and.returnValue(step);

            //when
            PlaygroundService.loadStep(step);

            //then
            expect($rootScope.$emit).not.toHaveBeenCalledWith('talend.loading.start');
            expect(PreparationService.getContent).not.toHaveBeenCalled();
        }));
    });

    it('should do nothing when provided name is the original name', inject(function($rootScope, PlaygroundService, PreparationService) {
        //given
        var name = 'My preparation';
        var newName = name;

        PlaygroundService.originalPreparationName = name;
        PlaygroundService.preparationName = newName;

        //when
        PlaygroundService.createOrUpdatePreparation(newName);
        $rootScope.$digest();

        //then
        expect(PreparationService.create).not.toHaveBeenCalled();
        expect(PreparationService.setName).not.toHaveBeenCalled();
        expect(PlaygroundService.preparationName).toBe(name);
        expect(PlaygroundService.originalPreparationName).toBe(name);
    }));

    describe('trasnformation', function() {
        var result, metadata;
        beforeEach(inject(function($rootScope, $q, PlaygroundService, PreparationService, DatagridService, RecipeService) {
            result = {
                'records': [{
                    'firstname': 'Grover',
                    'avgAmount': '82.4',
                    'city': 'BOSTON',
                    'birth': '01-09-1973',
                    'registration': '17-02-2008',
                    'id': '1',
                    'state': 'AR',
                    'nbCommands': '41',
                    'lastname': 'Quincy'
                }, {
                    'firstname': 'Warren',
                    'avgAmount': '87.6',
                    'city': 'NASHVILLE',
                    'birth': '11-02-1960',
                    'registration': '18-08-2007',
                    'id': '2',
                    'state': 'WA',
                    'nbCommands': '17',
                    'lastname': 'Johnson'
                }]
            };

            metadata = {id : 'e85afAa78556d5425bc2'};
            PlaygroundService.currentMetadata = metadata;

            spyOn($rootScope, '$emit').and.callThrough();
            spyOn(PreparationService, 'appendStep').and.returnValue($q.when(true));
            spyOn(PreparationService, 'getContent').and.returnValue($q.when({data: result}));
            spyOn(DatagridService, 'updateData').and.returnValue();
            spyOn(RecipeService, 'refresh').and.returnValue();
        }));

        it('should append preparation step', inject(function ($rootScope, PlaygroundService, PreparationService) {
            //given
            var action = 'uppercase';
            var column = {id: 'firstname'};
            var parameters = {param1: 'param1Value', param2: 4};

            //when
            PlaygroundService.appendStep(action, column, parameters);
            expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.start');
            $rootScope.$digest();

            //then
            expect(PreparationService.appendStep).toHaveBeenCalledWith(
                metadata,
                action,
                column,
                parameters
                );
            expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.stop');
        }));
    });
});