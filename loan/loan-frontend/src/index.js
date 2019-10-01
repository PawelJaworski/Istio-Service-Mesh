import * as React from 'react';
import { render } from 'react-dom';
import {WComponent} from "./js/WComponent";
import {ErrorMessage, ErrorMessagePropTypes} from "./ts/view/common/Messages";
import {InfoMessage, InfoMessagePropTypes} from "./ts/view/InfoMessage";

class ErrorMessageComponent extends WComponent {
  constructor() {
    super();
  }
  mount() {
    const propTypes = ErrorMessagePropTypes;
    const events = {};
    const props = {
      ...this.getProps(this.attributes, propTypes),
      ...this.getEvents(events),
      children: this.parseHtmlToReact(this.innerHTML)
    };
    render(<ErrorMessage {...props} />, this);
  }
}

class InfoMessageComponent extends WComponent {
  constructor() {
    super();
  }
  mount() {
    const propTypes = InfoMessagePropTypes;
    const events = {};
    const props = {
      ...this.getProps(this.attributes, propTypes),
      ...this.getEvents(events),
      children: this.parseHtmlToReact(this.innerHTML)
    };
    render(<InfoMessage {...props} />, this);
  }
}

customElements.define('loan-error', ErrorMessageComponent)
customElements.define('loan-info', InfoMessageComponent)