/*  ============================================================================

 Copyright (C) 2006-2018 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/


import { PLAYGROUND_PREPARATION_ROUTE } from '../../../index-route';

export default class PlaygroundHeaderCtrl {
	constructor(state, $state, StateService, HelpService, PlaygroundService, HistoryService, LookupService) {
		'ngInject';

		this.state = state;
		this.StateService = StateService;
		this.HelpService = HelpService;
		this.PlaygroundService = PlaygroundService;
		this.HistoryService = HistoryService;
		this.LookupService = LookupService;
		this.$state = $state;

		this.toggleNameEditMode = this.toggleNameEditMode.bind(this);
		this.editName = this.editName.bind(this);
	}

	toggleNameEditMode() {
		this.StateService.setNameEditionMode(true);
	}

	editName(event, { value }) {
		const cleanName = value.trim();
		if (!this.loading && cleanName) {
			this.loading = true;
			this.PlaygroundService.createOrUpdatePreparation(cleanName)
				.then(({ id }) => {
					this.$state.go(PLAYGROUND_PREPARATION_ROUTE, { prepid: id });
				})
				.finally(() => {
					this.StateService.setNameEditionMode(false);
					this.loading = false;
				});
		}
	}

	get subtitle() {
		const { nameEditionMode, dataset } = this.state.playground;
		return nameEditionMode ? '' : dataset.name;
	}

	get title() {
		return this.state.playground.preparation.name;
	}
}
