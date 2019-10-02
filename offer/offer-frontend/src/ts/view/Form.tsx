import * as React from "react";

export const NumberInput = (props) => {
    const {id} = props
    const {label} = props
    const {placeholder} = props
    const onChange = props.onChange || function() {}

    return (
        <React.Fragment>
            <div className="form-group">
                <label htmlFor={id}>{label}</label>
                <input type="number"
                       inputMode="numeric"
                       className="form-control"
                       id={id}
                       placeholder={placeholder}
                       onChange={onChange}
                />
            </div>
        </React.Fragment>
    )
}

export const TextInput = (props) => {
    const {id} = props
    const {label} = props
    const {placeholder} = props
    const onChange = props.onChange || function() {}

    return (
        <React.Fragment>
            <div className="form-group">
                <label htmlFor={id}>{label}</label>
                <input type="text"
                       className="form-control"
                       id={id}
                       placeholder={placeholder}
                       onChange={onChange}
                />
            </div>
        </React.Fragment>
    )
}

export const ReadOnly = (props) => {
    const {id} = props
    const {label} = props
    const {value} = props

    return (
        <React.Fragment>
            <div className="form-group">
                <label htmlFor={id}>{label}</label>
                <span className="form-control"
                      id={id}>
                    {value}
                </span>
            </div>
        </React.Fragment>
    )
}