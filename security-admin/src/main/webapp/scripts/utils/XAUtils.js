/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

 

define(function(require) {
    'use strict';
	
	var XAEnums		= require('utils/XAEnums');
	var localization	= require('utils/XALangSupport');
	var XAUtils = {};
	require('pnotify');
	
	// ///////////////////////////////////////////////////////
	// Enum utility methods
	// //////////////////////////////////////////////////////
	/**
	 * Get enum for the enumId
	 * @param {integer} enumId - The enumId
	 */
	XAUtils.getEnum = function(enumId) {
		if (!enumId || enumId.length < 1) {
			return "";
		}
		// check if the enums are loaded
		if (!XAEnums[enumId]) {
			return "";
		}
		return XAEnums[enumId];
	};

	/**
	 * Get enum by Enum and value
	 * @param {Object} myEnum - The enum
	 * @param {integer} value - The value
	 */
	XAUtils.enumElementByValue = function(myEnum, value) {
		var element = _.detect(myEnum, function(element) {
			return element.value == value;
		});
		return element;
	};

	/**
	 * Get enum by Enum and name, value
	 * @param {Object} myEnum - The enum
	 * @param {string} propertyName - The name of key
	 * @param {integer} propertyValue - The value
	 */
	XAUtils.enumElementByPropertyNameValue = function(myEnum, propertyName, propertyValue) {
		for ( var element in myEnum) {
			if (myEnum[element][propertyName] == propertyValue) {
				return myEnum[element];
			}
		}
		return null;
	};

	/**
	 * Get enum value for given enum label
	 * @param {Object} myEnum - The enum
	 * @param {string} label - The label to search for in the Enum
	 */
	XAUtils.enumLabelToValue = function(myEnum, label) {
		var element = _.detect(myEnum, function(element) {
			return element.label == label;
		});
		return (typeof element === "undefined") ? "--" : element.value;
	};

	/**
	 * Get enum label for given enum value
	 * @param {Object} myEnum - The enum
	 * @param {integer} value - The value
	 */
	XAUtils.enumValueToLabel = function(myEnum, value) {
		var element = _.detect(myEnum, function(element) {
			return element.value == value;
		});
		return (typeof element === "undefined") ? "--" : element.label;
	};

	/**
	 * Get enum label tt string for given Enum value
	 * @param {Object} myEnum - The enum
	 * @param {integer} value - The value
	 */
	XAUtils.enumValueToLabeltt = function(myEnum, value) {
		var element = _.detect(myEnum, function(element) {
			return element.value == value;
		});
		return (typeof element === "undefined") ? "--" : element.tt;
	};

	/**
	 * Get NVpairs for given Enum to be used in Select
	 * @param {Object} myEnum - The enum
	 */
	XAUtils.enumToSelectPairs = function(myEnum) {
		return _.map(myEnum, function(o){
			return {
				val : o.value,
				label : o.label
			};
		});
	};


	/**
	 * Get NVpairs for given Enum
	 * @param {Object} myEnum - The enum
	 */
	XAUtils.enumNVPairs = function(myEnum) {
		var nvPairs = {
			' ' : '--Select--'
		};

		for ( var name in myEnum) {
			nvPairs[myEnum[name].value] = myEnum[name].label;
		}

		return nvPairs;
	};

	/**
	 * Get array NV pairs for given Array
	 * @param {Array} myArray - The eArraynum
	 */
	XAUtils.arrayNVPairs = function(myArray) {
		var nvPairs = {
			' ' : '--Select--'
		};
		_.each(myArray, function(val) {
			nvPairs[val] = val;
		});
		return nvPairs;
	};

	/**
	 * Notify Info the given title / text
	 * @param {string} title - The title
	 * @param {string} text - The text
	 */
	XAUtils.notifyInfo = function(title, text) {
		$.pnotify({
			pnotify_title : title,
			pnotify_text : text
		});
	};

	/**
	 * Notify Error the given title / text
	 * @param {string} title - The title
	 * @param {string} text - The text
	 */
	XAUtils.notifyError = function(title, text) {
		$.pnotify({
			pnotify_title : title,
			pnotify_text : text,
			pnotify_type : 'error'
		});
	};
	
	/**
	 * Notify Success the given title / text
	 * @param {string} title - The title
	 * @param {string} text - The text
	 */
	XAUtils.notifySuccess = function(title, text) {
		$.pnotify({
			pnotify_title : title,
			pnotify_text : text,
			pnotify_type : 'success'
		});
	};
	
	
	/**
	 * Convert new line to <br />
	 * @param {string} str - the string to convert
	 */
	XAUtils.nl2br = function(str) {
		if (!str)
			return '';
		return str.replace(/\n/g, '<br/>').replace(/[\r\t]/g, " ");
	};

	/**
	 * Convert <br /> to new line 
	 * @param {string} str - the string to convert
	 */
	XAUtils.br2nl = function(str) {
		if (!str)
			return '';
		return str.replace(/\<br(\s*\/|)\>/gi, '\n');
	};

	/**
	 * Escape html chars
	 * @param {string} str - the html string to escape
	 */
	XAUtils.escapeHtmlChar = function(str) {
		if (!str)
			return '';
		str = str.replace(/&/g, "&amp;");
		str = str.replace(/>/g, "&gt;");
		str = str.replace(/</g, "&lt;");
		str = str.replace(/\"/g, "&quot;");
		str = str.replace(/'/g, "&#039;");
		return str;
	};

	/**
	 * nl2br and Escape html chars
	 * @param {string} str - the html string
	 */
	XAUtils.nl2brAndEscapeHtmlChar = function(str) {

		if (!str)
			return '';
		var escapedStr = escapeHtmlChar(str);
		var finalStr = nl2br(str);
		return finalStr;
	};

	/**
	 * prevent navigation with msg and call callback
	 * @param {String} msg - The msg to show
	 * @param {function} callback - The callback to call
	 */
	XAUtils.preventNavigation = function(msg, $form, callback) {
		$("body a, i[class^='icon-'],allowNav").on("click.blockNavigation", function(e) {
			XAUtils.preventNavigationHandler(e, msg, $form, callback);
		});
	};

	/**
	 * remove the block of preventNavigation
	 */
	XAUtils.allowNavigation = function() {
		$("body a, i[class^='icon-']").off('click.blockNavigation');
	};

	XAUtils.preventNavigationHandler = function(e, msg,$form,callback) {
		var formChanged = false;
		if(!_.isUndefined($form))
			formChanged = $form.find('.dirtyField').length > 0 ? true : false;
		if (!$(e.currentTarget).hasClass("_allowNav") && formChanged) {
			
			e.preventDefault();
			e.stopImmediatePropagation();
			XAUtils.alertPopup({
				msg : msg,
				title : "Warning",
				callback : callback
			});
			return false;
		}
	};

	/**
	 * Bootbox wrapper for alert
	 * @param {Object} params - The params
	 */
	XAUtils.alertPopup = function(params) {
		if(params.callback == undefined){
			bootbox.alert(params.msg);
		} else {
			bootbox.alert(params.msg,params.callback);
		}
	};

	/**
	 * Bootbox wrapper for confirm
	 * @param {Object} params - The params
	 */
	XAUtils.confirmPopup = function(params) {
		bootbox.confirm(params.msg, function(result) {
			if(result){
				params.callback();
			}
		}); 
	};	

	XAUtils.filterResultByIds = function(results , selectedVals){
		return _.filter(results,function(obj){
			if($.inArray(obj.id,selectedVals) < 0)
				return obj;
				
			});
	};
	XAUtils.filterResultByText = function(results , selectedVals){
		return _.filter(results,function(obj){
			if($.inArray(obj.text,selectedVals) < 0)
				return obj;
				
			});
	};
	XAUtils.scrollToField = function(field){
		$("html, body").animate({
			scrollTop: field.position().top-80
		}, 1100, function(){
			field.focus();
		});
	};
	XAUtils.blockUI = function(options){
		var Opt = {autoUnblock : false , clickUnblock : false, bgPath : 'images/' ,content: '<img src="images/blockLoading.gif" > Please wait..',css :{}};
		options = _.isUndefined(options)  ?  Opt : options;
		$.msg(options);
	};
	XAUtils.showGroups = function(rawValue){
		var showMoreLess = false,id;
		if(_.isArray(rawValue))
			rawValue =  new Backbone.Collection(rawValue);
		if(!_.isUndefined(rawValue) && rawValue.models.length > 0){
			var groupArr = _.uniq(_.compact(_.map(rawValue.models, function(m, i){
				if(m.has('groupName'))
					return m.get('groupName') ;
			})));
			if(groupArr.length > 0){
				if(rawValue.first().has('resourceId'))
					id = rawValue.first().get('resourceId');
				else
					id = rawValue.first().get('userId');
			}
			var newGroupArr = _.map(groupArr, function(name, i){
				if(i >=  4)
					return '<span class="label label-info" policy-group-id="'+id+'" style="display:none;">' + name + '</span>';
				else if(i == 3 && groupArr.length > 4){
					showMoreLess = true;
					return '<span class="label label-info" policy-group-id="'+id+'">' + name + '</span>';
				}
				else
					return '<span class="label label-info" policy-group-id="'+id+'">' + name + '</span>';
			});
			if(showMoreLess){
				newGroupArr.push('<span class="pull-left"><a href="javascript:void(0);" data-id="showMore" class="" policy-group-id="'+id+'"><code style=""> + More..</code></a></span><span class="pull-left" ><a href="javascript:void(0);" data-id="showLess" class="" policy-group-id="'+id+'" style="display:none;"><code> - Less..</code></a></span>');
			}
			return newGroupArr.length ? newGroupArr.join(' ') : '--';
		}else
			return '--';
	};
	XAUtils.showGroupsOrUsersForPolicy = function(rawValue, model, showGroups){
		var showMoreLess = false, groupArr = [];
		var type = _.isUndefined(showGroups) ? 'groups' : 'users';
		if(!_.isArray(rawValue) && !_.isUndefined(rawValue[type]))
			return '--';
		_.each(rawValue,function(perm){ 
			groupArr = _.union(groupArr, perm[type]) 
		});
		
		var newGroupArr = _.map(groupArr, function(name, i){
			if(i >=  4){
				return '<span class="label label-info" policy-'+type+'-id="'+model.id+'" style="display:none;">' + name + '</span>';
			}else if(i == 3 && groupArr.length > 4){
				showMoreLess = true;
				return '<span class="label label-info" policy-'+type+'-id="'+model.id+'">' + name + '</span>';
			}else{
				return '<span class="label label-info" policy-'+type+'-id="'+model.id+'">' + name + '</span>';
			}
		});
		if(showMoreLess){
			newGroupArr.push('<span class="pull-left"><a href="javascript:void(0);" data-id="showMore" class="" policy-'+type+'-id="'+model.id+'"><code style=""> + More..</code></a></span><span class="pull-left" ><a href="javascript:void(0);" data-id="showLess" class="" policy-'+type+'-id="'+model.id+'" style="display:none;"><code> - Less..</code></a></span>');
		}
		return newGroupArr.length ? newGroupArr.join(' ') : '--';
		
	};
	 
	XAUtils.defaultErrorHandler = function(model, error) {
		var App		= require('App');
		var vError = require('views/common/ErrorView');
        if (error.status == 404 ) {
           // trigger event or route to login here.
        	App.rContent.show(new vError({
        		pageNotFound :true
        	}));
        }else if (error.status == 401 ) {
        	window.location.replace('login.jsp');
        }
    };
    XAUtils.select2Focus  =  function(event) {
	    if (/^select2-focus/.test(event.type)) {
	        $(this).select2('open');
	    }
	};
	XAUtils.makeCollForGroupPermission = function(model){
		var XAEnums			= require('utils/XAEnums');
		var formInputColl = new Backbone.Collection(); 
		// permMapList = [ {id: 18, groupId : 1, permType :5}, {id: 18, groupId : 1, permType :4}, {id: 18, groupId : 2, permType :5} ]
		// [1] => [ {id: 18, groupId : 1, permType :5}, {id: 18, groupId : 1, permType :4} ]
		// [2] => [ {id: 18, groupId : 2, permType :5} ]
		if(!model.isNew()){
			if(!_.isUndefined(model.get('policyItems'))){
				var policyItems = model.get('policyItems'); 
//				var groupPolicyItems = _.filter(policyItems,function(m){if(!_.isEmpty(m.groups)) return m;});
				_.each(policyItems,function(obj){
					var groupNames = null, userNames = null;
					if(!_.isEmpty(obj.groups))	groupNames = obj.groups.join(',');
					if(!_.isEmpty(obj.users))	userNames = obj.users.join(',');
						var m = new Backbone.Model({
							groupName 	: groupNames,
							userName 	: userNames,
							accesses	: obj.accesses,
							conditions	: obj.conditions,
							delegateAdmin: obj.delegateAdmin,
							editMode 	: true,
						});
						formInputColl.add(m);
					
				});
			}
		}
		return formInputColl;
	};
	
	XAUtils.makeCollForUserPermission = function(model){
		var XAEnums			= require('utils/XAEnums');
		var coll = new Backbone.Collection(); 
		// permMapList = [ {id: 18, groupId : 1, permType :5}, {id: 18, groupId : 1, permType :4}, {id: 18, groupId : 2, permType :5} ]
		// [1] => [ {id: 18, groupId : 1, permType :5}, {id: 18, groupId : 1, permType :4} ]
		// [2] => [ {id: 18, groupId : 2, permType :5} ]
		if(!model.isNew()){
			if(!_.isUndefined(model.get('policyItems'))){
				var policyItems = model.get('policyItems'); 
				var userPolicyItems = _.filter(policyItems,function(m){if(!_.isEmpty(m.users)) return m;});
				_.each(userPolicyItems,function(obj){
						var m = new Backbone.Model({
//							userId 	: groupIds.join(','),
							userName 	: obj.users.join(','),
//							ipAddress	: values[0].ipAddress,
							editMode 	: true,
							accesses	: obj.accesses,
							conditions	: obj.conditions
						});
						coll.add(m);
					
				});
			}
		}
		return coll;
	};
	 XAUtils.checkDirtyField  =  function(arg1, arg2, $elem) {
		 if(_.isEqual(arg1,arg2)){
				$elem.removeClass('dirtyField');
			}else{
				$elem.addClass('dirtyField');
			}
	};
	XAUtils.checkDirtyFieldForToggle = function($el){
		if($el.hasClass('dirtyField')){
			$el.removeClass('dirtyField');
		}else{
			$el.addClass('dirtyField');
		}
	};
	XAUtils.checkDirtyFieldForSelect2 = function($el, dirtyFieldValue, that){
		if($el.hasClass('dirtyField') && _.isEqual($el.val(), dirtyFieldValue.toString())){
			$el.removeClass('dirtyField');
		}else if(!$el.hasClass('dirtyField')){
			$el.addClass('dirtyField');
			dirtyFieldValue =  !_.isUndefined(that.value.values) ? that.value.values : '';
		}
		return dirtyFieldValue;
	};
	XAUtils.enumToSelectLabelValuePairs = function(myEnum) {
		return _.map(myEnum, function(o){
			return {
				label : o.label,
				value : o.value+''
				//category :'DHSS',
			};
		});
	};
	XAUtils.hackForVSLabelValuePairs = function(myEnum) {
		return _.map(myEnum, function(o){
			return {
				label : o.label,
				value : o.label+''
				//category :'DHSS',
			};
		});
	};
	XAUtils.addVisualSearch = function(searchOpt,serverAttrName,collection,pluginAttr){
		var visualSearch ;
		var search = function(searchCollection, serverAttrName, searchOpt,collection){
			var params = {};
			searchCollection.each(function(m){
				var serverParamName = _.findWhere(serverAttrName,{text :m.attributes.category});
				var extraParam = {};
				if(_.has(serverParamName,'multiple') && serverParamName.multiple){
					extraParam[serverParamName.label] = XAUtils.enumLabelToValue(serverParamName.optionsArr,m.get('value'));;
					$.extend(params ,extraParam);
				}else{
					if(!_.isUndefined(serverParamName)){
						extraParam[serverParamName.label] = m.get('value');
						$.extend(params ,extraParam);
					}
				}
			});
			collection.queryParams = $.extend(collection.queryParams,params);
			collection.state.currentPage = collection.state.firstPage;
			collection.fetch({
				reset : true,
				cache : false
				//data : params,
			});
		};
		//var searchOpt = ['Event Time','User','Resource Name','Resource ID','Resource Type','Repository Name','Repository Type','Result','Client IP','Client Type','Access Type','Access Enforcer','Audit Type','Session ID'];
		
		var callbackCommon = {
	        search       : function(query, searchCollection) {
	        					collection.VSQuery = query;
	        					search(searchCollection, serverAttrName, searchOpt,collection);
	        			   },
	        facetMatches :  function(callback) {
	        				//	console.log(visualSearch);
	        					var searchOptTemp = $.extend(true, [], searchOpt);
	        					visualSearch.searchQuery.each(function(m){
	        						if($.inArray(m.get('category'),searchOptTemp) >= 0){
	        							searchOptTemp.splice($.inArray(m.get('category'),searchOptTemp), 1);
	        						} 
	        					});
	        					//visualSearch.options.readOnly = searchOptTemp.length <= 0 ? true : false;
	        					callback(searchOptTemp,{preserveOrder : false});
	        				},
	        removedFacet : function(removedFacet,searchCollection,indexObj){
					        	//console.log(removedFacet);
	        					
					        	var removedFacetSeverName = _.findWhere(serverAttrName,{text :removedFacet.get('category')});
					        	if(!_.isUndefined(removedFacetSeverName)){
					        		delete collection.queryParams[removedFacetSeverName.label];
					        		collection.state.currentPage = collection.state.firstPage;
					        		collection.fetch({
					        			reset : true,
					        			cache : false
					        		});
					        	}
					        	//TODO Added for Demo to remove datapicker popups
					        	if(!_.isUndefined(visualSearch.searchBox.$el))
					        		visualSearch.searchBox.$el.parents('body').find('.datepicker').remove();
					        }
	        //we can also add focus, blur events callback here..
	      };
		pluginAttr.callbacks =	$.extend(callbackCommon, pluginAttr.callbacks);
		//Initializing VisualSearch Plugin....
		visualSearch = VS.init($.extend(pluginAttr, {remainder : false }));
		
		if(visualSearch.searchQuery.length > 0) //For On Load Visual Search
			search(visualSearch.searchQuery, serverAttrName, searchOpt,collection);
		
		return visualSearch;
	};
	
	XAUtils.displayDatepicker = function ($el, facet, $date, callback) {
	    var input = $el.find('.search_facet.is_editing input.search_facet_input');
	    $el.parents('body').find('.datepicker').hide();
	    input.datepicker({
	    	autoclose : true,
	    	dateFormat: 'yy-mm-dd'
	    }).on('changeDate', function(ev){
	    	callback(ev.date);
	    	input.datepicker("hide");
	    	var e = jQuery.Event("keydown");
	    	e.which = 13; // Enter
	    	$(this).trigger(e);
	    });
	    if(!_.isUndefined($date)){
	    	if(facet == 'Start Date'){
	    		input.datepicker('setEndDate', $date);
	    	}else{
	    		input.datepicker('setStartDate', $date);
	    	}
	    }
	    input.datepicker('show');
	    input.on('blur',function(e){
	    	input.datepicker("hide");
	    	//$('.datepicker').remove();
	    	
	    });
	    //input.attr("readonly", "readonly");
	    input.on('keydown',function(e){
	    	if(e.which == 9 && e.shiftKey ){
	    		input.datepicker('setValue', new Date());
	    		input.trigger('change');
	    		input.datepicker("hide");
	    	}
	    	if(e.which == 13){
	    		var e1 = jQuery.Event("keypress");
	    		e1.which = 13; // Enter
	    		$(this).trigger(e1);
	    		
	    	}
	    });
	    return input;
	};
	XAUtils.getPerms = function(policyType){
		var permArr = [];
		switch(policyType){
		case XAEnums.AssetType.ASSET_HDFS.value :
			permArr = ['XA_PERM_TYPE_READ', 'XA_PERM_TYPE_WRITE', 'XA_PERM_TYPE_EXECUTE','XA_PERM_TYPE_ADMIN'];
			break;
		case XAEnums.AssetType.ASSET_HIVE.value :
			permArr = ['XA_PERM_TYPE_SELECT','XA_PERM_TYPE_UPDATE','XA_PERM_TYPE_CREATE','XA_PERM_TYPE_DROP','XA_PERM_TYPE_ALTER',
			           'XA_PERM_TYPE_INDEX','XA_PERM_TYPE_LOCK', 'XA_PERM_TYPE_ALL', 'XA_PERM_TYPE_ADMIN'];
			break;
		case XAEnums.AssetType.ASSET_HBASE.value :
			permArr = ['XA_PERM_TYPE_READ','XA_PERM_TYPE_WRITE','XA_PERM_TYPE_CREATE','XA_PERM_TYPE_ADMIN'];
			break;
		case XAEnums.AssetType.ASSET_KNOX.value :
			permArr = ['XA_PERM_TYPE_ALLOW','XA_PERM_TYPE_ADMIN'];
			break;
		case XAEnums.AssetType.ASSET_STORM.value :
			permArr = ['XA_PERM_TYPE_ADMIN'];
			/*permArr = ['XA_PERM_TYPE_SUBMIT_TOPOLOGY','XA_PERM_TYPE_FILE_UPLOAD','XA_PERM_TYPE_GET_NIMBUS',
			           'XA_PERM_TYPE_GET_CLUSTER_INFO','XA_PERM_TYPE_FILE_DOWNLOAD','XA_PERM_TYPE_KILL_TOPOLOGY',
			           'XA_PERM_TYPE_REBALANCE','XA_PERM_TYPE_ACTIVATE','XA_PERM_TYPE_DEACTIVATE','XA_PERM_TYPE_GET_TOPOLOGY_CONF',
			           'XA_PERM_TYPE_GET_TOPOLOGY','XA_PERM_TYPE_GET_USER_TOPOLOGY','XA_PERM_TYPE_GET_TOPOLOGY_INFO','XA_PERM_TYPE_UPLOAD_NEW_CREDENTIAL'
			           ];*/
			break;	
		}
		return permArr;
	};
	XAUtils.getPermHeaders = function(policyType, isGroup){
		if(_.isUndefined(isGroup))	isGroup = true;
		var permHeaders = isGroup ? [localization.tt('lbl.selectGroup')] :[localization.tt('lbl.selectUser')]; 
		
		switch(policyType){
		case XAEnums.AssetType.ASSET_HDFS.value :
			permHeaders.push(localization.tt('lbl.read'),localization.tt('lbl.write'),localization.tt('lbl.execute'),localization.tt('lbl.admin'),'');
			break;
		case XAEnums.AssetType.ASSET_HIVE.value :
			permHeaders.push(localization.tt('lbl.select'),localization.tt('lbl.update'),localization.tt('lbl.create'),localization.tt('lbl.drop'),localization.tt('lbl.alter'),localization.tt('lbl.index'),localization.tt('lbl.lock'),localization.tt('lbl.all'),localization.tt('lbl.admin'),'');
			break;
		case XAEnums.AssetType.ASSET_HBASE.value :
			permHeaders.push(localization.tt('lbl.read'),localization.tt('lbl.write'),localization.tt('lbl.create'),localization.tt('lbl.admin'),'');
			break;
		case XAEnums.AssetType.ASSET_KNOX.value :
			permHeaders.push(localization.tt('lbl.ipAddress'),localization.tt('lbl.allow'),localization.tt('lbl.admin'),'');
			break;
		case XAEnums.AssetType.ASSET_STORM.value :
			permHeaders.push(localization.tt('lbl.actions'),localization.tt('lbl.admin'),'');
			break;	
		}
		return permHeaders;
	};
	XAUtils.getStormActions = function(){
		return ['XA_PERM_TYPE_SUBMIT_TOPOLOGY','XA_PERM_TYPE_FILE_UPLOAD','XA_PERM_TYPE_GET_NIMBUS',
		           'XA_PERM_TYPE_GET_CLUSTER_INFO','XA_PERM_TYPE_FILE_DOWNLOAD','XA_PERM_TYPE_KILL_TOPOLOGY',
		           'XA_PERM_TYPE_REBALANCE','XA_PERM_TYPE_ACTIVATE','XA_PERM_TYPE_DEACTIVATE','XA_PERM_TYPE_GET_TOPOLOGY_CONF',
		           'XA_PERM_TYPE_GET_TOPOLOGY','XA_PERM_TYPE_GET_USER_TOPOLOGY','XA_PERM_TYPE_GET_TOPOLOGY_INFO','XA_PERM_TYPE_UPLOAD_NEW_CREDENTIAL'
		           ];
	};

	XAUtils.highlightDisabledPolicy = function(that){
		var $el = that.rTableList.$el;
		var timerId = setInterval(function(){
			if($el.find('tr td:last').text() != "No Policies found!"){
				_.each($el.find('tr td').find('.label-important'),function(a,b){
					  if($(a).html() == "Disabled")
					      console.log(that.$(a).parents('tr').addClass('disable-policy'))
				},that);	
				clearInterval(timerId);
			}
			console.log('highlight disabled policy..');
		},5);
	};
	XAUtils.showAlerForDisabledPolicy = function(that){
		if(!_.isUndefined(that.model.get('resourceStatus')) 
			&& that.model.get('resourceStatus') == XAEnums.ActiveStatus.STATUS_DISABLED.value){
			that.ui.policyDisabledAlert.show();
			that.$(that.rForm.el).addClass("policy-disabled");
		}else{
			that.ui.policyDisabledAlert.hide();
			that.$(that.rForm.el).removeClass("policy-disabled");
		}
	};
	XAUtils.customXEditableForPolicyCond = function(template) {
//		$.fn.editable.defaults.mode = 'inline';
		var PolicyConditions = function (options) {
	        this.init('policyConditions', options, PolicyConditions.defaults);
	    };

	    //inherit from Abstract input
	    $.fn.editableutils.inherit(PolicyConditions, $.fn.editabletypes.abstractinput);

	    $.extend(PolicyConditions.prototype, {   
	        render: function() {
	           this.$input = this.$tpl.find('input');
	           var pluginOpts = {tags : true,width :'220px',multiple: true,minimumInputLength: 1,tokenSeparators: [",", ";"],}
	           this.$input.select2(pluginOpts);
	        },
	        
	       value2str: function(value) {
	           var str = '';
	           if(value) {
	               for(var k in value) {
	                   str = str + k + ':' + value[k].toString() + ';';  
	               }
	           }
	           return str;
	       }, 
	       
	       value2input: function(value) {
	    	   _.each(value, function(val,name){
	    		   this.$input.filter('[name='+name+']').select2('val',value[name]);
	    	   },this);
	       },       
	           
	       input2value: function() {
	    	   var obj={};
	    	   _.each(this.$input,function(input){ 
	    		   var name = input.name;
	    		   var val = this.$input.filter('[name="'+name+'"]').select2('val'); 
	    		   obj[name] = val;
	    	   },this);
	    	   
	           return obj;
	       },
	       activate: function() {
	    	   this.$input.first().focus()
	       },
	    });

	    PolicyConditions.defaults = $.extend({}, $.fn.editabletypes.abstractinput.defaults, {
	        tpl: template,
	             
	        inputclass: ''
	    });
	    $.fn.editabletypes.policyConditions = PolicyConditions;
	};
	XAUtils.capitaliseFirstLetter = function (string){
	    return string.charAt(0).toUpperCase() + string.slice(1);
	};
	XAUtils.lowerCaseFirstLetter = function (string){
	    return string.charAt(0).toLowerCase() + string.slice(1);
	};
	XAUtils.getServicePoliciesURL = function(serviceId) {
		return "service/plugins/policies/service/"+serviceId;
	};
	XAUtils.getRangerServiceDef = function(name) {
		return "service/plugins/definitions/name/"+name;
	};
	
    return XAUtils;
});
