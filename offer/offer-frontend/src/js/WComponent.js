import {render, unmountComponentAtNode} from "react-dom";
import htmlToReact from "html-to-react";

export class WComponent extends HTMLElement {

    constructor() {
        super();
        this.observer = new MutationObserver(() => this.update());
        this.observer.observe(this, { attributes: true });
    }

    connectedCallback() {
        this._innerHTML = this.innerHTML;
        this.mount();
    }

    disconnectedCallback() {
        this.unmount();
        this.observer.disconnect();
    }

    update() {
        this.unmount();
        this.mount();
    }

    mount() {
        throw Error("Mount undefined");
    }

    unmount() {
        unmountComponentAtNode(this);
    }

    parseHtmlToReact(html) {
        return html && new htmlToReact.Parser().parse(html);
    }

    getProps(attributes, propTypes) {
        propTypes = propTypes|| {};
        return [ ...attributes ]
            .filter(attr => attr.name !== 'style')
            .map(attr => this.convert(propTypes, attr.name, attr.value))
            .reduce((props, prop) =>
                ({ ...props, [prop.name]: prop.value }), {});
    }

    getEvents(propTypes) {
        return Object.keys(propTypes)
            .filter(key => /on([A-Z].*)/.exec(key))
            .reduce((events, ev) => ({
                ...events,
                [ev]: args => {
                    const eventName = this.id + "." + ev
                    const eventArgs = {
                        detail: args
                    }
                    console.log("[CustomEvent] " + eventName + " {" + JSON.stringify(eventArgs) + "}")
                    document.dispatchEvent(new CustomEvent(eventName, eventArgs))
                }
            }), {});
    }

    convert(propTypes, attrName, attrValue) {
        const propName = Object.keys(propTypes)
            .find(key => key.toLowerCase() == attrName);
        let value = attrValue;
        if (attrValue === 'true' || attrValue === 'false')
            value = attrValue == 'true';
        else if (!isNaN(attrValue) && attrValue !== '')
            value = +attrValue;
        else if (/^{.*}/.exec(attrValue))
            value = JSON.parse(attrValue);
        return {
            name: propName ? propName : attrName,
            value: value
        };
    }

}