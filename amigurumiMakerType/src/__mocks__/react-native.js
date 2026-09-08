// src/__mocks__/react-native.js
// Minimal mock for react-native components used in tests

const React = require('react');

function createMockComponent(name) {
  const component = (props) => React.createElement(name, props, props.children);
  component.displayName = name;
  return component;
}

module.exports = {
  View: createMockComponent('View'),
  Text: createMockComponent('Text'),
  TouchableOpacity: createMockComponent('TouchableOpacity'),
  TextInput: createMockComponent('TextInput'),
  ScrollView: createMockComponent('ScrollView'),
  StyleSheet: {
    create: (styles) => styles,
    flatten: (style) => (Array.isArray(style) ? Object.assign({}, ...style.filter(Boolean)) : style || {}),
  },
  Platform: {
    OS: 'web',
    select: (obj) => obj.web || obj.default,
  },
  ActivityIndicator: createMockComponent('ActivityIndicator'),
  FlatList: createMockComponent('FlatList'),
  Image: createMockComponent('Image'),
  Modal: createMockComponent('Modal'),
  Pressable: createMockComponent('Pressable'),
  Switch: createMockComponent('Switch'),
};
