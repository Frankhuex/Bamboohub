import BookFilterPageWithURL from "./BookFilterPageWithURL";

export default function MyBookshelf() {
    return (<>
        <h1 className="text-xl mt-5 flex justify-center">这里陈列了您参与的书。</h1>
        <BookFilterPageWithURL url={"mine"} defaultClassifiedBy='roleType' defaultSortedBy="title" />
        </>
    )
}