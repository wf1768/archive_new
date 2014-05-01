/**
 * @license Copyright (c) 2003-2013, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.html or http://ckeditor.com/license
 */

//CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here. For example:
	// config.language = 'fr';
	// config.uiColor = '#AADC6E';
//};

CKEDITOR.editorConfig = function( config )
{
// Define changes to default configuration here. For example:
// config.language = 'zh-cn'; //配置语言
//config.uiColor = '#FFF'; //背景颜色
//config.width = 500; //宽度
//config.height = 500; //高度
 
//工具栏
config.toolbar =
[
    ['Bold','Italic','Underline','Strike','-','Subscript','Superscript'],
    ['NumberedList','BulletedList','-','Outdent','Indent','Blockquote'],
    ['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
    '/',
    ['Styles','Format','Font','FontSize'],
    ['TextColor','BGColor'],
 
];
};
