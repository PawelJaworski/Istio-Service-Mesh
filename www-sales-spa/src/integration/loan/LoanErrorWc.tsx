import React from "react";

export const LoanErrorWc = (props: {id: string, sourceId: string | undefined, sourceVersion: number | undefined}) => {
    const {sourceId} = props
    const {sourceVersion} = props

    if (sourceId === undefined || sourceVersion === undefined) {
        return null
    }

    const {id} = props
    return (
        <loan-error
            id={id}
            className="alert alert-danger"
            sourceId={sourceId}
            sourceVersion={sourceVersion}
        />
    )
}