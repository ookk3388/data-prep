/*  ============================================================================

  Copyright (C) 2006-2018 Talend Inc. - www.talend.com

  This source code is available under agreement available at
  https://github.com/Talend/data-prep/blob/master/LICENSE

  You should have received a copy of the agreement
  along with this program; if not, write to Talend SA
  9 rue Pages 92150 Suresnes, France

  ============================================================================*/

export default function ActionsSuggestionsCtrl($translate, state, TransformationService) {
	'ngInject';

	const vm = this;
	vm.TransformationService = TransformationService;
	vm.state = state;
	vm.scopes = [
		{
			key: 'column',
			label: $translate.instant('ACTIONS_TAB_COLUMN'),
			filterable: true,
		},
		{
			key: 'line',
			label: $translate.instant('ACTIONS_TAB_ROW'),
			filterable: false,
		},
		{
			key: 'dataset',
			label: $translate.instant('ACTIONS_TAB_TABLE'),
			filterable: true,
		},
	];
	vm.selectedScope = vm.scopes[0];

	vm.selectScope = (event, item) => {
		vm.selectedScope = item;
		vm.state.playground.filter.applyTransformationOnFilters = false;
	};
}
